package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.CardDto;
import org.springframework.validation.annotation.Validated;

@Validated
public abstract class CardDtoProcessor extends CommonDtoProcessor<CardDto, Long> {
}
