package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner"})
@ActiveProfiles({"default", "local", "bootstrap"})
class OwnerStampGrantDtoProcessorTest {
    @Autowired
    OwnerStampGrantDtoProcessor stampGrantDtoProcessor;
    @Autowired
    CardRepository cardRepository;

    @Test
    @Transactional
    void preprocessForPost() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertDoesNotThrow(() -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostNullIsDeleted() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(null)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostNullDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName(null)
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostBlankDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostLongDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name Dummy Display Name Dummy Display Name Dummy Display Name ")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostCardNotFound() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(10L)
                .numStamps(2)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    @WithMockUser(
            username = "notexist-2397-445e-89cd-8e817e0f441e",
            roles = {"owner"})
    void preprocessForPostUnauthorized() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(Throwable.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostCardInactive() {
        Card card = cardRepository.findById(1L).orElseThrow();
        card.setIsInactive(true);
        cardRepository.save(card);
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostZeroGrant() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(0)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostTooMuchGrant() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(3)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            stampGrantDtoProcessor.preprocessForPost(dto);
        });
    }

}