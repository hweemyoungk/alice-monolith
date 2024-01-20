package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.RedeemRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedeemRuleRepository extends JpaRepository<RedeemRule, Long> {
}