package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.CustomerMembershipDto;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"customer-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
@Transactional
class CustomerCardDtoProcessorTest {
    @Autowired
    CustomerCardDtoProcessor cardDtoProcessor;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardMapper cardMapper;

    @Autowired
    BlueprintRepository blueprintRepository;

    Blueprint blueprint;
    Card originalCard;

    @BeforeEach
    void setUp() {
        blueprint = blueprintRepository.findById(1L).orElseThrow();
        Card card = Card.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprint(blueprint)
                .build();
        originalCard = cardRepository.save(card);
    }

    @AfterEach
    void tearDown() {
        blueprint = null;
        cardRepository.deleteById(originalCard.getId());
        originalCard = null;
    }

    CardDto legalDto() {
        return CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(1L)
                .build();
    }

    @Test
    void preprocessForPost() {
        CardDto dto1 = legalDto();
        CardDto dto2 = legalDto();
        assertDoesNotThrow(() -> {
            cardDtoProcessor.preprocessForPost(List.of(dto1, dto2));
        });
    }

    @Test
    void preprocessForPostNullDtos() {
        assertThrows(ConstraintViolationException.class, () -> {
            cardDtoProcessor.preprocessForPost((Collection<CardDto>) null);
        });
    }

    @Test
    void preprocessForPostEmptyDtos() {
        assertThrows(ConstraintViolationException.class, () -> {
            cardDtoProcessor.preprocessForPost(new HashSet<>());
        });
    }

    // Validated by preprocessForPost(@NotEmpty @Valid Collection<CardDto> dtos)
    /*@Test
    void preprocessForPostNullBlueprintId() {
        CardDto dto1 = legalDto();
        dto1.setBlueprintId(null);
        CardDto dto2 = legalDto();
        dto2.setBlueprintId(2L);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPost(List.of(dto1, dto2));
        });
    }*/

    @Test
    void preprocessForPostBoundToMultipleBlueprints() {
        CardDto dto1 = legalDto();
        dto1.setBlueprintId(1L);
        CardDto dto2 = legalDto();
        dto2.setBlueprintId(2L);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPost(List.of(dto1, dto2));
        });
    }

    @Test
    void preprocessForPostExceedsNumMaxAccumulatedTotalCards() {
        CardDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    CustomerMembershipDto.builder()
                            .version(0)
                            .displayName("customer-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalCards(0)
                            .numMaxCurrentTotalCards(-1)
                            .numMaxCurrentActiveCards(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                cardDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    void preprocessForPostExceedsNumMaxMaxCurrentTotalCards() {
        CardDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    CustomerMembershipDto.builder()
                            .version(0)
                            .displayName("customer-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalCards(-1)
                            .numMaxCurrentTotalCards(0)
                            .numMaxCurrentActiveCards(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                cardDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    void preprocessForPostExceedsNumMaxCurrentActiveCards() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(1L)
                .build();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    CustomerMembershipDto.builder()
                            .version(0)
                            .displayName("customer-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalCards(-1)
                            .numMaxCurrentTotalCards(-1)
                            .numMaxCurrentActiveCards(0)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                cardDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    void preprocessForPostSingle() {
        CardDto dto = legalDto();
        assertDoesNotThrow(() -> {
            cardDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    void preprocessForPostSingleNullIsDiscarded() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(null)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(1L)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            cardDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    void preprocessForPostSingleBlueprintNotFound() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(10L)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            cardDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    void preprocessForPostSingleNotDefaultValues() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(true)
                .isUsedOut(true)
                .isInactive(true)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(1L)
                .build();
        CardDto preprocessedForPost = cardDtoProcessor.preprocessForPostSingle(dto);
        assertFalse(preprocessedForPost.getIsDeleted());
        assertFalse(preprocessedForPost.getIsDiscarded());
        assertFalse(preprocessedForPost.getIsUsedOut());
        assertFalse(preprocessedForPost.getIsInactive());
        assertEquals(0, preprocessedForPost.getNumCollectedStamps());
        assertEquals(0, preprocessedForPost.getNumRedeemed());
    }

    @Test
    void preprocessForPostSingleTooMuchNumGoalStamps() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps() + 1)
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .blueprintId(1L)
                .build();
        CardDto preprocessedForPost = cardDtoProcessor.preprocessForPostSingle(dto);
        assertEquals(blueprint.getNumMaxStamps(), preprocessedForPost.getNumGoalStamps());
    }

    @Test
    void preprocessForPostSingleUnknownCustomerId() {
        CardDto dto = CardDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .numCollectedStamps(blueprint.getNumMaxStamps()) // blueprint: 50
                .numGoalStamps(blueprint.getNumMaxStamps())
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .isFavorite(false)
                .numRedeemed(blueprint.getNumMaxRedeems() - 1) // blueprint: 5
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .customerId(UUID.randomUUID())
                .blueprintId(1L)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPut() {
        CardDto dto = cardMapper.toDto(originalCard);
        assertDoesNotThrow(() -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTrySoftDelete() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setIsDeleted(true);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTrySetIsDiscardedTrueToFalse() {
        originalCard.setIsDiscarded(true);
        cardRepository.save(originalCard);
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setIsDiscarded(false);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyIsUsedOut() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setIsUsedOut(true);
        CardDto preprocessedForPut = cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        assertFalse(preprocessedForPut.getIsUsedOut());
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyNumCollectedStamps() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setNumCollectedStamps(1000);
        cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        assertEquals(originalCard.getNumCollectedStamps(), dto.getNumCollectedStamps());
    }

    @Test
    @Transactional
    void preprocessForPutTooMuchNumGoalStamps() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setNumGoalStamps(blueprint.getNumMaxStamps() + 1);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyNumRedeemed() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setNumRedeemed(1000);
        CardDto preprocessedForPut = cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        assertEquals(originalCard.getNumRedeemed(), (int) preprocessedForPut.getNumRedeemed());
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyCustomerId() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setCustomerId(UUID.randomUUID());
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyBlueprintId() {
        CardDto dto = cardMapper.toDto(originalCard);
        dto.setBlueprintId(3L);
        assertThrows(DtoProcessingException.class, () -> {
            cardDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }
}