package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface OwnerRedeemRuleRepository extends RedeemRuleRepository {
    /**
     * Fetches parent blueprint and store.
     *
     * @param id must not be {@literal null}.
     * @return
     */
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    @Query("select r from RedeemRule r left join fetch Blueprint b on r.blueprint.id = b.id left join fetch Store s on b.store.id = s.id where r.id = :id")
    Optional<RedeemRule> findById(Long id);

    @Override
    @PostFilter("authentication.name == filterObject.blueprint.store.ownerId.toString()")
    Set<RedeemRule> findByBlueprint_IdAndIdIn(Long id, Collection<Long> ids);
}
