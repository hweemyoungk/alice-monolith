package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedeemMapperImpl implements RedeemMapper {
    private final EntityManager entityManager;

    @Override
    public Redeem toEntity(RedeemDto dto) {
        if (dto == null) {
            return null;
        }

        Redeem.RedeemBuilder<?, ?> redeem = Redeem.builder();

        redeem.id(dto.getId());
        redeem.version(dto.getVersion());
        redeem.displayName(dto.getDisplayName());
        redeem.createdDate(dto.getCreatedDate());
        redeem.lastModifiedDate(dto.getLastModifiedDate());
        redeem.isDeleted(dto.getIsDeleted());
        redeem.numStampsBefore(dto.getNumStampsBefore());
        redeem.numStampsAfter(dto.getNumStampsAfter());
        redeem.token(dto.getToken());
        redeem.redeemRule(entityManager.getReference(RedeemRule.class, dto.getRedeemRuleId()));
        redeem.card(entityManager.getReference(Card.class, dto.getCardId()));

        return redeem.build();
    }

    @Override
    public RedeemDto toDto(Redeem entity) {
        if (entity == null) {
            return null;
        }

        RedeemDto.RedeemDtoBuilder<?, ?> redeemDto = RedeemDto.builder();

        redeemDto.id(entity.getId());
        redeemDto.version(entity.getVersion());
        redeemDto.displayName(entity.getDisplayName());
        redeemDto.createdDate(entity.getCreatedDate());
        redeemDto.lastModifiedDate(entity.getLastModifiedDate());
        redeemDto.isDeleted(entity.getIsDeleted());
        redeemDto.numStampsBefore(entity.getNumStampsBefore());
        redeemDto.numStampsAfter(entity.getNumStampsAfter());
        redeemDto.token(entity.getToken());
        redeemDto.redeemRuleId(entity.getRedeemRule().getId());
        redeemDto.cardId(entity.getCard().getId());

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
            entity.setVersion(dto.getVersion());
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
        if (dto.getNumStampsBefore() != null) {
            entity.setNumStampsBefore(dto.getNumStampsBefore());
        }
        if (dto.getNumStampsAfter() != null) {
            entity.setNumStampsAfter(dto.getNumStampsAfter());
        }
        if (dto.getToken() != null) {
            entity.setToken(dto.getToken());
        }
        if (dto.getRedeemRuleId() != null) {
            entity.setRedeemRule(
                    entityManager.getReference(RedeemRule.class, dto.getRedeemRuleId()));
        }
        if (dto.getCardId() != null) {
            entity.setCard(
                    entityManager.getReference(Card.class, dto.getCardId()));
        }

        return entity;
    }
}
