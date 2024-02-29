package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.repositories.RedeemRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AdminRedeemRepository extends RedeemRepository {
    @Transactional
    @Modifying
    @Query("delete from Redeem r where r.redeemRule is null and r.card is null")
    int deleteByRedeemRuleIsNullAndCardIsNull();
}
