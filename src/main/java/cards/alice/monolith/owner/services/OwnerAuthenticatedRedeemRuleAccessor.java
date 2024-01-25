package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerAuthenticatedRedeemRuleAccessor implements AuthenticatedEntityAccessor<RedeemRule, Long> {
    private final RedeemRuleRepository redeemRuleRepository;

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    public Optional<RedeemRule> findById(Long id) {
        return redeemRuleRepository.findById(id);
    }
}
