package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.StampGrantDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class StampGrantDtoProcessor extends CommonDtoProcessor<StampGrantDto, Long> {
}
