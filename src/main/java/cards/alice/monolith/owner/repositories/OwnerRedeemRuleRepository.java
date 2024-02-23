package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Owner can only query RedeemRule that is NOT DELETED<br>
 * and where RedeemRule.blueprint.store.ownerId matches current user.
 */
public interface OwnerRedeemRuleRepository extends RedeemRuleRepository {
    /**
     * Fetches parent blueprint and store.
     *
     * @param id must not be {@literal null}.
     */
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    @Query("""
            select r from RedeemRule r
            left join fetch Blueprint b on r.blueprint.id = b.id
            left join fetch Store s on b.store.id = s.id
            where r.id = :id
            and r.isDeleted = false""")
    Optional<RedeemRule> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    @Query("""
            select r from RedeemRule r
            left join fetch Blueprint b on r.blueprint.id = b.id
            left join fetch Store s on b.store.id = s.id
            where r.id = :id
            and r.isDeleted = false""")
    Optional<RedeemRule> exclusiveFindById(Long id);

    @Override
    @PostFilter("authentication.name == filterObject.blueprint.store.ownerId.toString()")
    @Query("""
            select r from RedeemRule r
            where (:id is null or r.blueprint.id = :id)
            and r.id in :ids
            and r.isDeleted = false""")
    Set<RedeemRule> findByBlueprint_IdAndIdIn(@Param("id") @Nullable Long id, @Param("ids") @NonNull Collection<Long> ids);

    @Override
    @PostFilter("authentication.name == filterObject.blueprint.store.ownerId.toString()")
    @Query("""
            select r from RedeemRule r
            where r.blueprint.id = :id
            and r.isDeleted = false""")
    Set<RedeemRule> findByBlueprint_Id(@Param("id") Long id);
}
