package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.repositories.RedeemRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

public interface AdminRedeemRepository extends RedeemRepository {
    @Transactional
    @Modifying
    @Query("delete from Redeem r where r.redeemRule = :redeemRule and r.card = :card")
    int deleteByRedeemRuleAndCard(@Nullable RedeemRule redeemRule, @Nullable Card card);
}
