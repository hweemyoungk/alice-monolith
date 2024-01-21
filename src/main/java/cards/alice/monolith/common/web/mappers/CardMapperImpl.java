package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CardMapperImpl implements CardMapper {
    private final EntityManager entityManager;

    @Override
    public Card toEntity(CardDto cardDto) {
        if (cardDto == null) {
            return null;
        }

        Card.CardBuilder<?, ?> card = Card.builder();

        card.id(cardDto.getId());
        card.version(cardDto.getVersion());
        card.displayName(cardDto.getDisplayName());
        card.createdDate(cardDto.getCreatedDate());
        card.lastModifiedDate(cardDto.getLastModifiedDate());
        card.isDeleted(cardDto.getIsDeleted());
        card.numCollectedStamps(cardDto.getNumCollectedStamps());
        card.numGoalStamps(cardDto.getNumGoalStamps());
        card.isFavorite(cardDto.getIsFavorite());
        card.numRedeemed(cardDto.getNumRedeemed());
        card.customerId(cardDto.getCustomerId());
        card.blueprint(entityManager.getReference(Blueprint.class, cardDto.getBlueprintId()));
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
        cardDto.createdDate(card.getCreatedDate());
        cardDto.lastModifiedDate(card.getLastModifiedDate());
        cardDto.isDeleted(card.getIsDeleted());
        cardDto.displayName(card.getDisplayName());
        cardDto.numCollectedStamps(card.getNumCollectedStamps());
        cardDto.numGoalStamps(card.getNumGoalStamps());
        cardDto.expirationDate(card.getBlueprint().getExpirationDate());
        cardDto.isFavorite(card.getIsFavorite());
        cardDto.numRedeemed(card.getNumRedeemed());
        cardDto.customerId(card.getCustomerId());
        cardDto.storeId(card.getBlueprint().getStore().getId());
        cardDto.blueprintId(card.getBlueprint().getId());
        cardDto.bgImageId(card.getBgImageId());
        cardDto.isDiscarded(card.getIsDiscarded());
        cardDto.isUsedOut(card.getIsUsedOut());
        cardDto.isInactive(card.getIsInactive());

        return cardDto.build();
    }

    @Override
    public Card partialUpdate(CardDto cardDto, Card card) {
        if (cardDto == null) {
            return card;
        }

        if (cardDto.getId() != null) {
            card.setId(cardDto.getId());
        }
        if (cardDto.getVersion() != null) {
            card.setVersion(cardDto.getVersion());
        }
        if (cardDto.getDisplayName() != null) {
            card.setDisplayName(cardDto.getDisplayName());
        }
        if (cardDto.getCreatedDate() != null) {
            card.setCreatedDate(cardDto.getCreatedDate());
        }
        if (cardDto.getLastModifiedDate() != null) {
            card.setLastModifiedDate(cardDto.getLastModifiedDate());
        }
        if (cardDto.getIsDeleted() != null) {
            card.setIsDeleted(cardDto.getIsDeleted());
        }
        if (cardDto.getNumCollectedStamps() != null) {
            card.setNumCollectedStamps(cardDto.getNumCollectedStamps());
        }
        if (cardDto.getNumGoalStamps() != null) {
            card.setNumGoalStamps(cardDto.getNumGoalStamps());
        }
        if (cardDto.getIsFavorite() != null) {
            card.setIsFavorite(cardDto.getIsFavorite());
        }
        if (cardDto.getNumRedeemed() != null) {
            card.setNumRedeemed(cardDto.getNumRedeemed());
        }
        if (cardDto.getCustomerId() != null) {
            card.setCustomerId(cardDto.getCustomerId());
        }
        /*if (cardDto.getStoreId() != null) {
            card.setStore(
                    entityManager.getReference(Store.class, cardDto.getStoreId()));
        }*/
        if (cardDto.getBlueprintId() != null) {
            card.setBlueprint(
                    entityManager.getReference(Blueprint.class, cardDto.getBlueprintId()));
        }
        if (cardDto.getBgImageId() != null) {
            card.setBgImageId(cardDto.getBgImageId());
        }
        if (cardDto.getIsDiscarded() != null) {
            card.setIsDiscarded(cardDto.getIsDiscarded());
        }
        if (cardDto.getIsUsedOut() != null) {
            card.setIsUsedOut(cardDto.getIsUsedOut());
        }
        if (cardDto.getIsInactive() != null) {
            card.setIsInactive(cardDto.getIsInactive());
        }

        return card;
    }
}
