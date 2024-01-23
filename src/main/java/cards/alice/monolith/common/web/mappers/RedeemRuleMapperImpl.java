package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedeemRuleMapperImpl implements RedeemRuleMapper {
    private final EntityManager entityManager;

    @Override
    public RedeemRule toEntity(RedeemRuleDto redeemRuleDto) {
        if (redeemRuleDto == null) {
            return null;
        }

        RedeemRule.RedeemRuleBuilder<?, ?> redeemRule = RedeemRule.builder()
                .id(redeemRuleDto.getId())
                .version(redeemRuleDto.getVersion())
                .displayName(redeemRuleDto.getDisplayName())
                .createdDate(redeemRuleDto.getCreatedDate())
                .lastModifiedDate(redeemRuleDto.getLastModifiedDate())
                .isDeleted(redeemRuleDto.getIsDeleted())
                .description(redeemRuleDto.getDescription())
                .consumes(redeemRuleDto.getConsumes())
                .imageId(redeemRuleDto.getImageId())
                .blueprint(
                        // If redeemRuleDto is from brand new blueprintDto, blueprintId is null.
                        redeemRuleDto.getBlueprintId() == null ? null : entityManager.getReference(
                                Blueprint.class, redeemRuleDto.getBlueprintId()));

        return redeemRule.build();
    }

    @Override
    public RedeemRuleDto toDto(RedeemRule redeemRule) {
        if (redeemRule == null) {
            return null;
        }

        RedeemRuleDto.RedeemRuleDtoBuilder<?, ?> redeemRuleDto = RedeemRuleDto.builder()
                .id(redeemRule.getId())
                .version(redeemRule.getVersion())
                .displayName(redeemRule.getDisplayName())
                .createdDate(redeemRule.getCreatedDate())
                .lastModifiedDate(redeemRule.getLastModifiedDate())
                .isDeleted(redeemRule.getIsDeleted())
                .description(redeemRule.getDescription())
                .consumes(redeemRule.getConsumes())
                .imageId(redeemRule.getImageId())
                .blueprintId(redeemRule.getBlueprint().getId());

        return redeemRuleDto.build();
    }

    @Override
    public RedeemRule partialUpdate(RedeemRuleDto redeemRuleDto, RedeemRule redeemRule) {
        if (redeemRuleDto == null) {
            return redeemRule;
        }

        if (redeemRuleDto.getId() != null) {
            redeemRule.setId(redeemRuleDto.getId());
        }
        if (redeemRuleDto.getVersion() != null) {
            redeemRule.setVersion(redeemRuleDto.getVersion());
        }
        if (redeemRuleDto.getDisplayName() != null) {
            redeemRule.setDisplayName(redeemRuleDto.getDisplayName());
        }
        if (redeemRuleDto.getCreatedDate() != null) {
            redeemRule.setCreatedDate(redeemRuleDto.getCreatedDate());
        }
        if (redeemRuleDto.getLastModifiedDate() != null) {
            redeemRule.setLastModifiedDate(redeemRuleDto.getLastModifiedDate());
        }
        if (redeemRuleDto.getIsDeleted() != null) {
            redeemRule.setIsDeleted(redeemRuleDto.getIsDeleted());
        }
        if (redeemRuleDto.getDescription() != null) {
            redeemRule.setDescription(redeemRuleDto.getDescription());
        }
        if (redeemRuleDto.getConsumes() != null) {
            redeemRule.setConsumes(redeemRuleDto.getConsumes());
        }
        if (redeemRuleDto.getImageId() != null) {
            redeemRule.setImageId(redeemRuleDto.getImageId());
        }
        if (redeemRuleDto.getBlueprintId() != null) {
            redeemRule.setBlueprint(entityManager.getReference(
                    Blueprint.class, redeemRuleDto.getBlueprintId()));
        }

        return redeemRule;
    }
}
