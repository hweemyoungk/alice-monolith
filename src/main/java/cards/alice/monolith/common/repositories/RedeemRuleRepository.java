package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface RedeemRuleRepository extends JpaRepository<RedeemRule, Long> {
    @Query("select r from RedeemRule r where r.blueprint.id = :id")
    Set<RedeemRule> findByBlueprint_Id(@Param("id") Long id);
}