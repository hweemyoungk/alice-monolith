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

/**
 * Currently, OwnerStampGrantDtoProcessor#checkMembershipForPost is NO-OP.<br>
 * So the only inner test target is OwnerStampGrantDtoProcessor#preprocessForPost,<br>
 * which can just be tested by OwnerStampGrantDtoProcessor+preprocessForPostSingle.
 */
@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
class OwnerStampGrantDtoProcessorTest {
    @Autowired
    OwnerStampGrantDtoProcessor stampGrantDtoProcessor;
    @Autowired
    CardRepository cardRepository;

    @Test
    @Transactional
    void preprocessForPostSingle() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertDoesNotThrow(() -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleNullIsDeleted() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(null)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleNullDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName(null)
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleBlankDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleLongDisplayName() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name Dummy Display Name Dummy Display Name Dummy Display Name ")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleCardNotFound() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(10L)
                .numStamps(2)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @WithMockUser(
            username = "notexist-2397-445e-89cd-8e817e0f441e",
            roles = {"owner"})
    void preprocessForPostSingleUnauthorized() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(2)
                .build();
        assertThrows(Throwable.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleCardInactive() {
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
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleZeroGrant() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(0)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleTooMuchGrant() {
        StampGrantDto dto = StampGrantDto.builder()
                .id(-1L)
                .version(-1)
                .isDeleted(true)
                .displayName("Dummy Display Name")
                .cardId(1L)
                .numStamps(3)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            stampGrantDtoProcessor.preprocessForPostSingle(dto);
        });
    }

}