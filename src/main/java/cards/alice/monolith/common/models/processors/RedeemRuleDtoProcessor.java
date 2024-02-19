package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.RedeemRuleDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class RedeemRuleDtoProcessor extends CommonDtoProcessor<RedeemRuleDto, Long> {
}
