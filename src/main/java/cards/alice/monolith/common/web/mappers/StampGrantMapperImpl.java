package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.StampGrant;
import cards.alice.monolith.common.models.StampGrantDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StampGrantMapperImpl implements StampGrantMapper {
    private final EntityManager entityManager;

    @Override
    public StampGrant toEntity(StampGrantDto stampGrantDto) {
        if (stampGrantDto == null) {
            return null;
        }

        StampGrant.StampGrantBuilder<?, ?> stampGrant = StampGrant.builder();

        stampGrant.id(stampGrantDto.getId());
        stampGrant.version(stampGrantDto.getVersion());
        stampGrant.displayName(stampGrantDto.getDisplayName());
        stampGrant.createdDate(stampGrantDto.getCreatedDate());
        stampGrant.lastModifiedDate(stampGrantDto.getLastModifiedDate());
        stampGrant.isDeleted(stampGrantDto.getIsDeleted());
        stampGrant.card(entityManager.getReference(
                Card.class, stampGrantDto.getCardId()));
        stampGrant.numStamps(stampGrantDto.getNumStamps());

        return stampGrant.build();
    }

    @Override
    public StampGrantDto toDto(StampGrant stampGrant) {
        if (stampGrant == null) {
            return null;
        }

        StampGrantDto.StampGrantDtoBuilder<?, ?> stampGrantDto = StampGrantDto.builder();

        stampGrantDto.id(stampGrant.getId());
        stampGrantDto.version(stampGrant.getVersion());
        stampGrantDto.displayName(stampGrant.getDisplayName());
        stampGrantDto.createdDate(stampGrant.getCreatedDate());
        stampGrantDto.lastModifiedDate(stampGrant.getLastModifiedDate());
        stampGrantDto.isDeleted(stampGrant.getIsDeleted());
        stampGrantDto.cardId(stampGrant.getCard().getId());
        stampGrantDto.numStamps(stampGrant.getNumStamps());

        return stampGrantDto.build();
    }

    @Override
    public StampGrant partialUpdate(StampGrantDto stampGrantDto, StampGrant stampGrant) {
        if (stampGrantDto == null) {
            return stampGrant;
        }

        if (stampGrantDto.getId() != null) {
            stampGrant.setId(stampGrantDto.getId());
        }
        if (stampGrantDto.getVersion() != null) {
            stampGrant.setVersion(stampGrantDto.getVersion());
        }
        if (stampGrantDto.getDisplayName() != null) {
            stampGrant.setDisplayName(stampGrantDto.getDisplayName());
        }
        if (stampGrantDto.getCreatedDate() != null) {
            stampGrant.setCreatedDate(stampGrantDto.getCreatedDate());
        }
        if (stampGrantDto.getLastModifiedDate() != null) {
            stampGrant.setLastModifiedDate(stampGrantDto.getLastModifiedDate());
        }
        if (stampGrantDto.getIsDeleted() != null) {
            stampGrant.setIsDeleted(stampGrantDto.getIsDeleted());
        }
        if (stampGrantDto.getCardId() != null) {
            stampGrant.setCard(entityManager.getReference(
                    Card.class, stampGrantDto.getCardId())
            );
        }
        if (stampGrantDto.getNumStamps() != null) {
            stampGrant.setNumStamps(stampGrantDto.getNumStamps());
        }

        return stampGrant;
    }
}
