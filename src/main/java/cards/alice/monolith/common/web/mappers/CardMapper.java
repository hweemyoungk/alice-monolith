package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import org.mapstruct.*;

public interface CardMapper {
    Card toEntity(CardDto cardDto);

    CardDto toDto(Card card);

    Card partialUpdate(CardDto cardDto, @MappingTarget Card card);
}