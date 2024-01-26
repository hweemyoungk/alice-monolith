package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface OwnerRedeemRuleRepository extends RedeemRuleRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    Optional<RedeemRule> findById(Long aLong);

    @Override
    @PostFilter("authentication.name == filterObject.blueprint.store.ownerId.toString()")
    Set<RedeemRule> findByBlueprint_IdAndIdIn(Long id, Collection<Long> ids);
}
