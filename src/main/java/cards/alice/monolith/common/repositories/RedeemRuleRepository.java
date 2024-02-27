package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface RedeemRuleRepository extends JpaRepository<RedeemRule, Long> {
    /*@Query("""
            select count(r) from RedeemRule r
            where r.blueprint.id = :id
            and r.blueprint.store.ownerId = :ownerId
            and r.isDeleted = :isDeleted""")*/
    @Query(value = """
            select count(0) from (select 0 from redeem_rule as r
            left join blueprint as b on r.blueprint_id = b.id
            left join store as s on b.store_id = s.id
            where b.id = :id
            and s.owner_id = :ownerId
            and r.is_deleted = :isDeleted
            for update) as sq""",
            nativeQuery = true)
    long exclusiveCountByBlueprint_IdAndBlueprint_Store_OwnerIdAndIsDeleted(@Param("id") @NonNull Long id, @Param("ownerId") @NonNull UUID ownerId, @Param("isDeleted") @NonNull Boolean isDeleted);

    @Query("select r from RedeemRule r where (:id is null or r.blueprint.id = :id) and r.id in :ids")
    Set<RedeemRule> findByBlueprint_IdAndIdIn(@Param("id") @Nullable Long id, @Param("ids") @NonNull Collection<Long> ids);

    @Query("select r from RedeemRule r where r.blueprint.id = :id")
    Set<RedeemRule> findByBlueprint_Id(@Param("id") Long id);
}