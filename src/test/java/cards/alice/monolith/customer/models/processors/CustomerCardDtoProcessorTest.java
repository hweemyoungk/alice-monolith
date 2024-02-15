package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner"})
@ActiveProfiles({"default", "common", "dev", "monolith", "h2", "oauth2_https", "bootstrap"})
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

    @Test
    void preprocessForPost() {
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
        assertDoesNotThrow(() -> {
            cardDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPostBlueprintNotFound() {
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
            cardDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPostNotDefaultValues() {
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
        CardDto preprocessedForPost = cardDtoProcessor.preprocessForPost(dto);
        assertFalse(preprocessedForPost.getIsDeleted());
        assertFalse(preprocessedForPost.getIsDiscarded());
        assertFalse(preprocessedForPost.getIsUsedOut());
        assertFalse(preprocessedForPost.getIsInactive());
        assertEquals(0, preprocessedForPost.getNumCollectedStamps());
        assertEquals(0, preprocessedForPost.getNumRedeemed());
    }

    @Test
    void preprocessForPostTooMuchNumGoalStamps() {
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
        CardDto preprocessedForPost = cardDtoProcessor.preprocessForPost(dto);
        assertEquals(blueprint.getNumMaxStamps(), preprocessedForPost.getNumGoalStamps());
    }

    @Test
    void preprocessForPostUnknownCustomerId() {
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
            cardDtoProcessor.preprocessForPost(dto);
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