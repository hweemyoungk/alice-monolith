package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.StoreDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class StoreDtoProcessor extends CommonDtoProcessor<StoreDto, Long> {
}
