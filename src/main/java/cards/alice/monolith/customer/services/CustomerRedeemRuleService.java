package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRuleDto;

import java.util.Set;

public interface CustomerRedeemRuleService {
    Set<RedeemRuleDto> listRedeemRules(Long blueprintId);
}
