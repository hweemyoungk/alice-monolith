package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.BlueprintDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class BlueprintDtoProcessor extends CommonDtoProcessor<BlueprintDto, Long> {
}
