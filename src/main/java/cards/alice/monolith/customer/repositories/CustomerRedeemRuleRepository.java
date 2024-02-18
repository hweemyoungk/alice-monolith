package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * Customer can only query RedeemRule that is NOT DELETED and where RedeemRule.blueprint is PUBLISHING.
 */
public interface CustomerRedeemRuleRepository extends RedeemRuleRepository {
    @Override
    @Query("""
            select r from RedeemRule r
            where r.blueprint.id = :id
            and r.blueprint.isPublishing
            and r.isDeleted=false""")
    Set<RedeemRule> findByBlueprint_Id(Long id);
}
