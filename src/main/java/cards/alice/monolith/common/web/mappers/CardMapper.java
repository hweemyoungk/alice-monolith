package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import org.mapstruct.*;

public interface CardMapper extends BaseMapper<Card, CardDto> {}