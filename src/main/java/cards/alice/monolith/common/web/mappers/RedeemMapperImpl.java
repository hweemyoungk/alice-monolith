package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedeemMapperImpl implements RedeemMapper {
    private final RedeemRuleRepository redeemRuleRepository;
    private final CardRepository cardRepository;

    private final RedeemRuleMapper redeemRuleMapper;
    private final CardMapper cardMapper;

    @Override
    public Redeem toEntity(RedeemDto redeemDto) {
        if (redeemDto == null) {
            return null;
        }

        Redeem.RedeemBuilder<?, ?> redeem = Redeem.builder();

        redeem.id(redeemDto.getId());
        //redeem.version(redeemDto.getVersion());
        redeem.displayName(redeemDto.getDisplayName());
        redeem.createdDate(redeemDto.getCreatedDate());
        redeem.lastModifiedDate(redeemDto.getLastModifiedDate());
        redeem.isDeleted(redeemDto.getIsDeleted());
        redeem.redeemRequestId(redeemDto.getRedeemRequestId());
        redeem.numStampsBefore(redeemDto.getNumStampsBefore());
        redeem.numStampsAfter(redeemDto.getNumStampsAfter());
        redeem.redeemRule(redeemRuleRepository.getReferenceById(redeemDto.getRedeemRuleId()));
        redeem.card(cardRepository.getReferenceById(redeemDto.getCardId()));

        return redeem.build();
    }

    @Override
    public RedeemDto toDto(Redeem redeem) {
        if (redeem == null) {
            return null;
        }

        RedeemDto.RedeemDtoBuilder<?, ?> redeemDto = RedeemDto.builder();

        redeemDto.id(redeem.getId());
        //redeemDto.version(redeem.getVersion());
        redeemDto.displayName(redeem.getDisplayName());
        redeemDto.createdDate(redeem.getCreatedDate());
        redeemDto.lastModifiedDate(redeem.getLastModifiedDate());
        redeemDto.isDeleted(redeem.getIsDeleted());
        redeemDto.redeemRequestId(redeem.getRedeemRequestId());
        redeemDto.numStampsBefore(redeem.getNumStampsBefore());
        redeemDto.numStampsAfter(redeem.getNumStampsAfter());
        redeemDto.redeemRuleDto(!PERSISTENCE_UTIL.isLoaded(redeem, "redeemRule") ?
                null :
                redeemRuleMapper.toDto(redeem.getRedeemRule()));
        redeemDto.redeemRuleId(redeem.getRedeemRule() == null ? null : redeem.getRedeemRule().getId());
        redeemDto.cardDto(!PERSISTENCE_UTIL.isLoaded(redeem, "card") ?
                null :
                cardMapper.toDto(redeem.getCard()));
        redeemDto.cardId(redeem.getCard() == null ? null : redeem.getCard().getId());

        return redeemDto.build();
    }

    @Override
    public Redeem partialUpdate(RedeemDto dto, Redeem entity) {
        if (dto == null) {
            return entity;
        }

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        if (dto.getVersion() != null) {
            //entity.setVersion(dto.getVersion());
        }
        if (dto.getDisplayName() != null) {
            entity.setDisplayName(dto.getDisplayName());
        }
        if (dto.getCreatedDate() != null) {
            entity.setCreatedDate(dto.getCreatedDate());
        }
        if (dto.getLastModifiedDate() != null) {
            entity.setLastModifiedDate(dto.getLastModifiedDate());
        }
        if (dto.getIsDeleted() != null) {
            entity.setIsDeleted(dto.getIsDeleted());
        }
        if (dto.getRedeemRequestId() != null) {
            entity.setRedeemRequestId(dto.getRedeemRequestId());
        }
        if (dto.getNumStampsBefore() != null) {
            entity.setNumStampsBefore(dto.getNumStampsBefore());
        }
        if (dto.getNumStampsAfter() != null) {
            entity.setNumStampsAfter(dto.getNumStampsAfter());
        }
        if (dto.getRedeemRuleId() != null) {
            entity.setRedeemRule(
                    redeemRuleRepository.getReferenceById(dto.getRedeemRuleId()));
        }
        if (dto.getCardId() != null) {
            entity.setCard(
                    cardRepository.getReferenceById(dto.getCardId()));
        }

        return entity;
    }
}
