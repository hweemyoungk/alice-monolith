package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRequestNewDto;
import cards.alice.monolith.common.models.processors.RedeemRequestDtoProcessor;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.repositories.CustomerCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomerRedeemRequestDtoProcessor implements RedeemRequestDtoProcessor {
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

    private final CustomerCardRepository cardRepository;
    private final RedeemRuleRepository redeemRuleRepository;

    @Override
    public RedeemRequestNewDto preprocessForPost(RedeemRequestNewDto dto) {
        final Set<String> violationMessages = new HashSet<>();
        final Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, dto.getCardId()));
        if (!(card.getCustomerId().equals(dto.getCustomerId()))) {
            violationMessages.add("Card doesn't belong to current customer");
        }

        final Long redeemRuleId = dto.getRedeemRuleId();
        final RedeemRule redeemRule = redeemRuleRepository.findById(redeemRuleId)
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, redeemRuleId));
        if (!card.getBlueprint().equals(redeemRule.getBlueprint())) {
            violationMessages.add("Blueprint of card and that of redeemRule don't match");
        }

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess RedeemRequestDto", violationMessages);
        }

        // id, version must be null
        dto.preprocessBaseForNew();
        // isDeleted must be false
        dto.setIsDeleted(false);

        // customerId
        // Validated by @PreAuthorize postRedeemRequest

        // customerDisplayName, cardId, redeemRuleId
        // Validated by @Validated

        // isRedeemed must be false
        dto.setIsRedeemed(false);

        // Set blueprintDisplayName
        final Blueprint blueprint = card.getBlueprint();
        dto.setBlueprintDisplayName(blueprint.getDisplayName());

        // Set ownerId
        dto.setOwnerId(blueprint.getStore().getOwnerId());

        // Set expMilliseconds
        dto.setExpMillisecondsFromNow(watchRedeemRequestDurationSeconds * 1000);

        return dto;
    }

    @Override
    public RedeemRequestNewDto preprocessForPut(String s, RedeemRequestNewDto dto) {
        throw new UnsupportedOperationException();
    }
}
