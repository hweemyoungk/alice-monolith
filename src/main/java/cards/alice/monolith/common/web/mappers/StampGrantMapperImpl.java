package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.StampGrant;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StampGrantMapperImpl implements StampGrantMapper {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    @Override
    public StampGrant toEntity(StampGrantDto stampGrantDto) {
        if (stampGrantDto == null) {
            return null;
        }

        StampGrant.StampGrantBuilder<?, ?> stampGrant = StampGrant.builder();

        stampGrant.id(stampGrantDto.getId());
        //stampGrant.version(stampGrantDto.getVersion());
        stampGrant.displayName(stampGrantDto.getDisplayName());
        stampGrant.createdDate(stampGrantDto.getCreatedDate());
        stampGrant.lastModifiedDate(stampGrantDto.getLastModifiedDate());
        stampGrant.isDeleted(stampGrantDto.getIsDeleted());
        stampGrant.numStamps(stampGrantDto.getNumStamps());
        stampGrant.card(cardRepository.getReferenceById(stampGrantDto.getCardId()));

        return stampGrant.build();
    }

    @Override
    public StampGrantDto toDto(StampGrant stampGrant) {
        if (stampGrant == null) {
            return null;
        }

        StampGrantDto.StampGrantDtoBuilder<?, ?> stampGrantDto = StampGrantDto.builder();

        stampGrantDto.id(stampGrant.getId());
        //stampGrantDto.version(stampGrant.getVersion());
        stampGrantDto.displayName(stampGrant.getDisplayName());
        stampGrantDto.createdDate(stampGrant.getCreatedDate());
        stampGrantDto.lastModifiedDate(stampGrant.getLastModifiedDate());
        stampGrantDto.isDeleted(stampGrant.getIsDeleted());
        stampGrantDto.numStamps(stampGrant.getNumStamps());
        stampGrantDto.cardDto(!PERSISTENCE_UTIL.isLoaded(stampGrant, "card") ?
                null :
                cardMapper.toDto(stampGrant.getCard()));
        stampGrantDto.cardId(stampGrant.getCard().getId());

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
            //stampGrant.setVersion(stampGrantDto.getVersion());
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
        if (stampGrantDto.getNumStamps() != null) {
            stampGrant.setNumStamps(stampGrantDto.getNumStamps());
        }
        if (stampGrantDto.getCardId() != null) {
            stampGrant.setCard(cardRepository
                    .getReferenceById(stampGrantDto.getCardId()));
        }

        return stampGrant;
    }
}
