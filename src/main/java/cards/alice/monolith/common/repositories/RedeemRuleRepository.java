package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;

public interface RedeemRuleRepository extends JpaRepository<RedeemRule, Long> {
    @Query("select r from RedeemRule r where (:id is null or r.blueprint.id = :id) and r.id in :ids")
    Set<RedeemRule> findByBlueprint_IdAndIdIn(@Param("id") @Nullable Long id, @Param("ids") @NonNull Collection<Long> ids);

    @Query("select r from RedeemRule r where r.blueprint.id = :id")
    Set<RedeemRule> findByBlueprint_Id(@Param("id") Long id);
}