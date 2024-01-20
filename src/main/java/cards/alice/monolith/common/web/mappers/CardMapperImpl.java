package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapperImpl implements CardMapper {
    private final DateMapper dateMapper;

    @Override
    public Card toEntity(CardDto cardDto) {
        if (cardDto == null) {
            return null;
        }

        Card.CardBuilder<?, ?> card = Card.builder();

        card.id(cardDto.getId());
        card.version(cardDto.getVersion());
        card.displayName(cardDto.getDisplayName());
        card.createdDate(dateMapper.asTimestamp(cardDto.getCreatedDate()));
        card.lastModifiedDate(dateMapper.asTimestamp(cardDto.getLastModifiedDate()));
        card.isDeleted(cardDto.getIsDeleted());
        card.numCollectedStamps(cardDto.getNumCollectedStamps());
        card.numGoalStamps(cardDto.getNumGoalStamps());
        card.expirationDate(dateMapper.asTimestamp(cardDto.getExpirationDate()));
        card.isFavorite(cardDto.getIsFavorite());
        card.numRedeemed(cardDto.getNumRedeemed());
        card.customerId(cardDto.getCustomerId());
        card.storeId(cardDto.getStoreId());
        card.blueprintId(cardDto.getBlueprintId());
        card.bgImageId(cardDto.getBgImageId());
        card.isDiscarded(cardDto.getIsDiscarded());
        card.isUsedOut(cardDto.getIsUsedOut());
        card.isInactive(cardDto.getIsInactive());

        return card.build();
    }

    @Override
    public CardDto toDto(Card card) {
        if (card == null) {
            return null;
        }

        CardDto.CardDtoBuilder<?, ?> cardDto = CardDto.builder();

        cardDto.id(card.getId());
        cardDto.version(card.getVersion());
        cardDto.displayName(card.getDisplayName());
        cardDto.createdDate(dateMapper.asOffsetDateTime(card.getCreatedDate()));
        cardDto.lastModifiedDate(dateMapper.asOffsetDateTime(card.getLastModifiedDate()));
        cardDto.isDeleted(card.getIsDeleted());
        cardDto.numCollectedStamps(card.getNumCollectedStamps());
        cardDto.numGoalStamps(card.getNumGoalStamps());
        cardDto.expirationDate(dateMapper.asOffsetDateTime(card.getExpirationDate()));
        cardDto.isFavorite(card.getIsFavorite());
        cardDto.numRedeemed(card.getNumRedeemed());
        cardDto.customerId(card.getCustomerId());
        cardDto.storeId(card.getStoreId());
        cardDto.blueprintId(card.getBlueprintId());
        cardDto.bgImageId(card.getBgImageId());
        cardDto.isDiscarded(card.getIsDiscarded());
        cardDto.isUsedOut(card.getIsUsedOut());
        cardDto.isInactive(card.getIsInactive());

        return cardDto.build();
    }
}

