package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedeemRuleMapperImpl implements RedeemRuleMapper {
    private final DateMapper dateMapper;

    @Override
    public RedeemRule toEntity(RedeemRuleDto redeemRuleDto) {
        if (redeemRuleDto == null) {
            return null;
        }

        RedeemRule.RedeemRuleBuilder redeemRule = RedeemRule.builder()
                .id(redeemRuleDto.getId())
                .version(redeemRuleDto.getVersion())
                .displayName(redeemRuleDto.getDisplayName())
                .createdDate(dateMapper.asTimestamp(redeemRuleDto.getCreatedDate()))
                .lastModifiedDate(dateMapper.asTimestamp(redeemRuleDto.getLastModifiedDate()))
                .isDeleted(redeemRuleDto.getIsDeleted())
                .description(redeemRuleDto.getDescription())
                .consumes(redeemRuleDto.getConsumes())
                .imageId(redeemRuleDto.getImageId())
                //.blueprint(redeemRuleDto.
                .blueprintId(redeemRuleDto.getBlueprintId());

        return redeemRule.build();
    }

    @Override
    public RedeemRuleDto toDto(RedeemRule redeemRule) {
        if (redeemRule == null) {
            return null;
        }

        RedeemRuleDto.RedeemRuleDtoBuilder redeemRuleDto = RedeemRuleDto.builder()
                .id(redeemRule.getId())
                .version(redeemRule.getVersion())
                .displayName(redeemRule.getDisplayName())
                .createdDate(dateMapper.asOffsetDateTime(redeemRule.getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(redeemRule.getLastModifiedDate()))
                .isDeleted(redeemRule.getIsDeleted())
                .description(redeemRule.getDescription())
                .consumes(redeemRule.getConsumes())
                .imageId(redeemRule.getImageId())
                //.blueprint(redeemRule.
                .blueprintId(redeemRule.getBlueprintId());

        return redeemRuleDto.build();
    }
}
