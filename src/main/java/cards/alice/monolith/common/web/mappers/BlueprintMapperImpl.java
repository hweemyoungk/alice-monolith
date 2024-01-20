package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BlueprintMapperImpl implements BlueprintMapper {
    private final DateMapper dateMapper;
    //private final StoreMapper storeMapper;
    private final RedeemRuleMapper redeemRuleMapper;

    @Override
    public Blueprint toEntity(BlueprintDto blueprintDto) {
        if (blueprintDto == null) {
            return null;
        }

        Blueprint.BlueprintBuilder blueprint = Blueprint.builder()
                .id(blueprintDto.getId())
                .version(blueprintDto.getVersion())
                .displayName(blueprintDto.getDisplayName())
                .createdDate(dateMapper.asTimestamp(blueprintDto.getCreatedDate()))
                .lastModifiedDate(dateMapper.asTimestamp(blueprintDto.getLastModifiedDate()))
                .isDeleted(blueprintDto.getIsDeleted())
                .description(blueprintDto.getDescription())
                .stampGrantCondDescription(blueprintDto.getStampGrantCondDescription())
                .numMaxStamps(blueprintDto.getNumMaxStamps())
                .numMaxRedeems(blueprintDto.getNumMaxRedeems())
                .numMaxIssues(blueprintDto.getNumMaxIssues())
                .expirationDate(dateMapper.asTimestamp(blueprintDto.getExpirationDate()))
                .bgImageId(blueprintDto.getBgImageId())
                .isPublishing(blueprintDto.getIsPublishing())
                .redeemRules(blueprintDto.getRedeemRuleDtos().stream()
                        .map(redeemRuleMapper::toEntity)
                        .collect(Collectors.toSet()))
                //.store(storeMapper.storeDtoToStore(blueprintDto.getStore()))
                .storeId(blueprintDto.getStoreId());

        return blueprint.build();
    }

    @Override
    public BlueprintDto toDto(Blueprint blueprint) {
        if (blueprint == null) {
            return null;
        }

        BlueprintDto.BlueprintDtoBuilder blueprintDto = BlueprintDto.builder()
                .id(blueprint.getId())
                .version(blueprint.getVersion())
                .displayName(blueprint.getDisplayName())
                .createdDate(dateMapper.asOffsetDateTime(blueprint.getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(blueprint.getLastModifiedDate()))
                .isDeleted(blueprint.getIsDeleted())
                .description(blueprint.getDescription())
                .stampGrantCondDescription(blueprint.getStampGrantCondDescription())
                .numMaxStamps(blueprint.getNumMaxStamps())
                .numMaxRedeems(blueprint.getNumMaxRedeems())
                .numMaxIssues(blueprint.getNumMaxIssues())
                .expirationDate(dateMapper.asOffsetDateTime(blueprint.getExpirationDate()))
                .bgImageId(blueprint.getBgImageId())
                .isPublishing(blueprint.getIsPublishing())
                .redeemRuleDtos(blueprint.getRedeemRules().stream()
                        .map(redeemRuleMapper::toDto)
                        .collect(Collectors.toSet()))
                //.store(storeMapper.storeToStoreDto(blueprint.getStore()))
                .storeId(blueprint.getStoreId());

        return blueprintDto.build();
    }
}
