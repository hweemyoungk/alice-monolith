package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.RedeemRequestNewDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Currently, CustomerRedeemRequestDtoProcessor#checkMembershipForPost is NO-OP.<br>
 * So the only inner test target is CustomerRedeemRequestDtoProcessor#preprocessForPost,<br>
 * which can just be tested by CustomerRedeemRequestDtoProcessor+preprocessForPostSingle.
 */
@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"customer-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
class CustomerRedeemRequestDtoProcessorTest {
    private final UUID uuid = UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e");
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

    @Autowired
    CustomerRedeemRequestDtoProcessor redeemRequestDtoProcessor;

    @Autowired
    EntityManager entityManager;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private BlueprintRepository blueprintRepository;
    @Autowired
    private RedeemRuleRepository redeemRuleRepository;

    RedeemRequestNewDto legalDto() {
        return RedeemRequestNewDto.builder()
                .id(UUID.randomUUID().toString())
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .cardId(1L)
                .redeemRuleId(1L)
                .customerDisplayName("A Customer Display Name")
                .customerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                //.ttlSeconds(1L)
                //.ownerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                //.blueprintDisplayName(null)
                //.expMilliseconds(1L)
                //.isRedeemed(true)
                .build();
    }

    @Test
    @Transactional
    void preprocessForPostSingle() {
        RedeemRequestNewDto dto = legalDto();
        assertDoesNotThrow(() -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
        //assertThrows(ConstraintViolationException.class, () -> {
        //    redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        //});
        //assertThrows(ResourceNotFoundException.class, () -> {
        //    redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        //});
        //assertThrows(DtoProcessingException.class, () -> {
        //    redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        //});
        //assertThrows(Throwable.class, () -> {
        //    redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        //});
    }

    @Test
    @Transactional
    void preprocessForPostSingleCardNotFound() {
        RedeemRequestNewDto dto = legalDto();

        cardRepository.deleteById(dto.getCardId());

        assertThrows(ResourceNotFoundException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleCardNotOwned() {
        RedeemRequestNewDto dto = legalDto();

        Card card = cardRepository.findById(dto.getCardId()).orElseThrow();
        card.setCustomerId(UUID.randomUUID());
        cardRepository.save(card);

        assertThrows(AccessDeniedException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleRedeemRuleNotFound() {
        RedeemRequestNewDto dto = legalDto();
        redeemRuleRepository.deleteById(dto.getRedeemRuleId());

        assertThrows(ResourceNotFoundException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleBlueprintNotPublishing() {
        RedeemRequestNewDto dto = legalDto();
        Blueprint blueprint = cardRepository.findById(dto.getCardId()).orElseThrow()
                .getBlueprint();
        blueprint.setIsPublishing(false);
        blueprintRepository.save(blueprint);

        assertThrows(DtoProcessingException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleBlueprintMismatch() {
        final Card cardOfOtherBlueprint = Card.builder()
                .displayName("My First Card")
                .numCollectedStamps(0)
                .numGoalStamps(1)
                .isFavorite(true)
                .numRedeemed(0)
                .customerId(uuid)
                .blueprint(entityManager.getReference(Blueprint.class, 2L))
                .bgImageId(null)
                .isDiscarded(false)
                .isUsedOut(false)
                .isInactive(false)
                .build();
        final Card savedCardOfOtherBlueprint = cardRepository.save(cardOfOtherBlueprint);

        RedeemRequestNewDto dto = legalDto();
        dto.setCardId(savedCardOfOtherBlueprint.getId());
        assertThrows(DtoProcessingException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleUnknownCustomerId() {
        RedeemRequestNewDto dto = legalDto();
        dto.setCustomerId(UUID.randomUUID());
        assertThrows(DtoProcessingException.class, () -> {
            redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleNotDefaultValues() {
        RedeemRequestNewDto dto = legalDto();
        RedeemRequestNewDto preprocessedForPost = redeemRequestDtoProcessor.preprocessForPostSingle(dto);
        assertNull(preprocessedForPost.getId());
        assertFalse(preprocessedForPost.getIsDeleted());
        assertEquals(preprocessedForPost.getTtlSeconds(), watchRedeemRequestDurationSeconds);
        assertEquals(preprocessedForPost.getOwnerId(), uuid);
        //assertEquals(preprocessedForPost.getExpMilliseconds(), watchRedeemRequestDurationSeconds);
        assertFalse(preprocessedForPost.getIsRedeemed());
    }
}