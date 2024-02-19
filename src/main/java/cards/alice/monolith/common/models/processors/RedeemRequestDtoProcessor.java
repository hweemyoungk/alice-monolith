package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.RedeemRequestNewDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class RedeemRequestDtoProcessor extends CommonDtoProcessor<RedeemRequestNewDto, String> {
}
