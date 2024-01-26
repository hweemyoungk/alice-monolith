package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemRuleDto;

import java.util.Set;

public interface OwnerRedeemRuleService {
    Set<RedeemRuleDto> listRedeemRules(Long blueprintId, Set<Long> ids);

    Set<RedeemRuleDto> saveRedeemRules(Set<RedeemRuleDto> redeemRuleDtos);
}
