package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemRuleDto;

import java.util.Optional;
import java.util.Set;

public interface OwnerRedeemRuleService {
    Optional<RedeemRuleDto> getRedeemRuleById(Long id);

    Set<RedeemRuleDto> listRedeemRules(Long blueprintId, Set<Long> ids);

    Set<RedeemRuleDto> saveRedeemRules(Set<RedeemRuleDto> redeemRuleDtos);

    RedeemRuleDto saveNewRedeemRule(RedeemRuleDto redeemRuleDto);

    Optional<RedeemRuleDto> updateRedeemRuleById(Long id, RedeemRuleDto redeemRuleDto);

    Optional<RedeemRuleDto> patchRedeemRuleById(Long id, RedeemRuleDto redeemRuleDto);
}
