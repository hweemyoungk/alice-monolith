package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRequestNewDto;
import cards.alice.monolith.common.models.processors.RedeemRequestDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.repositories.CustomerCardRepository;
import cards.alice.monolith.customer.repositories.CustomerRedeemRuleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class CustomerRedeemRequestDtoProcessor implements RedeemRequestDtoProcessor {
    @Value("${cards.alice.customer.app.watch-redeem-request-duration-seconds}")
    private long watchRedeemRequestDurationSeconds;

    private final CustomerCardRepository cardRepository;
    private final CustomerRedeemRuleRepository redeemRuleRepository;

    @Override
    public RedeemRequestNewDto preprocessForPost(@Valid RedeemRequestNewDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id
        // Overwrite to default
        // version
        // Ignored in entity
        // @NotNull isDeleted
        // Validated by @Valid
        // Overwrite to default
        dto.preprocessBaseForNew();

        // @NotBlank @Length(max = 30) displayName
        // Validated by @Valid

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotNull @Positive cardId;
        // Validated by @Valid
        // Card must exist
        // CustomerCardRepository verifies
        // : Customer must own card
        final Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, dto.getCardId()));

        // @NotNull @Positive redeemRuleId;
        // Validated by @Valid
        // RedeemRule must exist
        final RedeemRule redeemRule = redeemRuleRepository.findById(dto.getRedeemRuleId())
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, dto.getRedeemRuleId()));
        // RedeemRule.blueprint must be active
        final Blueprint blueprint = redeemRule.getBlueprint();
        if (!blueprint.getIsPublishing()) {
            violationMessages.add("Blueprint must be publishing");
        }
        // RedeemRule.blueprint must match Card.blueprint
        if (!card.getBlueprint().getId().equals(blueprint.getId())) {
            violationMessages.add("Blueprint of card and that of redeemRule don't match");
        }

        // @NotBlank @Length(max = 30) customerDisplayName;
        // Validated by @Valid

        // @NotNull customerId;
        // Validated by @Valid
        // Must match current user's name
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!Objects.equals(username, dto.getCustomerId().toString())) {
            violationMessages.add("Unidentified customer ID provided");
        }

        // @Positive ttlSeconds;
        // Validated by @Valid
        // Overwrite to default
        dto.setTtlSeconds(watchRedeemRequestDurationSeconds);

        // ownerId;
        // Overwrite
        dto.setOwnerId(blueprint.getStore().getOwnerId());

        // private String blueprintDisplayName;
        // Overwrite
        dto.setBlueprintDisplayName(blueprint.getDisplayName());

        // @Positive expMilliseconds;
        // Overwrite to default
        dto.setExpMillisecondsFromNow(1_000 * watchRedeemRequestDurationSeconds);

        // private Boolean isRedeemed;
        // Overwrite to default
        dto.setIsRedeemed(Boolean.FALSE);

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess RedeemRequestDto", violationMessages);
        }

        return dto;
    }

    @Override
    public RedeemRequestNewDto preprocessForPut(String id, RedeemRequestNewDto dto) {
        throw new UnsupportedOperationException();
    }
}
