package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BlueprintMapperImpl implements BlueprintMapper {
    private final StoreMapper storeMapper;
    private final RedeemRuleMapper redeemRuleMapper;

    private final StoreRepository storeRepository;

    public BlueprintMapperImpl(StoreMapper storeMapper, @Lazy RedeemRuleMapper redeemRuleMapper, StoreRepository storeRepository) {
        this.storeMapper = storeMapper;
        this.redeemRuleMapper = redeemRuleMapper;
        this.storeRepository = storeRepository;
    }

    @Override
    public Blueprint toEntity(BlueprintDto blueprintDto) {
        if (blueprintDto == null) {
            return null;
        }

        Blueprint.BlueprintBuilder<?, ?> blueprint = Blueprint.builder()
                .id(blueprintDto.getId())
                //.version(blueprintDto.getVersion())
                .displayName(blueprintDto.getDisplayName())
                .createdDate(blueprintDto.getCreatedDate())
                .lastModifiedDate(blueprintDto.getLastModifiedDate())
                .isDeleted(blueprintDto.getIsDeleted())
                .description(blueprintDto.getDescription())
                .stampGrantCondDescription(blueprintDto.getStampGrantCondDescription())
                .numMaxStamps(blueprintDto.getNumMaxStamps())
                .numMaxRedeems(blueprintDto.getNumMaxRedeems())
                .numMaxIssuesPerCustomer(blueprintDto.getNumMaxIssuesPerCustomer())
                .numMaxIssues(blueprintDto.getNumMaxIssues())
                .expirationDate(blueprintDto.getExpirationDate())
                .bgImageId(blueprintDto.getBgImageId())
                .isPublishing(blueprintDto.getIsPublishing())
                .store(storeRepository.getReferenceById(blueprintDto.getStoreId()))
                .redeemRules(blueprintDto.getRedeemRuleDtos() == null ?
                        null :
                        blueprintDto.getRedeemRuleDtos().stream()
                                .map(redeemRuleMapper::toEntity)
                                .collect(Collectors.toSet()));

        return blueprint.build();
    }

    @Override
    public BlueprintDto toDto(Blueprint blueprint) {
        if (blueprint == null) {
            return null;
        }

        BlueprintDto.BlueprintDtoBuilder<?, ?> blueprintDto = BlueprintDto.builder()
                .id(blueprint.getId())
                //.version(blueprint.getVersion())
                .displayName(blueprint.getDisplayName())
                .createdDate(blueprint.getCreatedDate())
                .lastModifiedDate(blueprint.getLastModifiedDate())
                .isDeleted(blueprint.getIsDeleted())
                .description(blueprint.getDescription())
                .stampGrantCondDescription(blueprint.getStampGrantCondDescription())
                .numMaxStamps(blueprint.getNumMaxStamps())
                .numMaxRedeems(blueprint.getNumMaxRedeems())
                .numMaxIssuesPerCustomer(blueprint.getNumMaxIssuesPerCustomer())
                .numMaxIssues(blueprint.getNumMaxIssues())
                .expirationDate(blueprint.getExpirationDate())
                .bgImageId(blueprint.getBgImageId())
                .isPublishing(blueprint.getIsPublishing())
                .storeDto(!PERSISTENCE_UTIL.isLoaded(blueprint, "store") ?
                        null :
                        storeMapper.toDto(blueprint.getStore()))
                .storeId(blueprint.getStore() == null ? null : blueprint.getStore().getId())
                .redeemRuleDtos(!PERSISTENCE_UTIL.isLoaded(blueprint, "redeemRules") || blueprint.getRedeemRules() == null ?
                        null :
                        blueprint.getRedeemRules().stream()
                                .map(redeemRuleMapper::toDto)
                                .collect(Collectors.toSet()));

        return blueprintDto.build();
    }

    @Override
    public Blueprint partialUpdate(BlueprintDto blueprintDto, Blueprint blueprint) {
        if (blueprintDto == null) {
            return blueprint;
        }

        if (blueprintDto.getId() != null) {
            blueprint.setId(blueprintDto.getId());
        }
        if (blueprintDto.getVersion() != null) {
            //blueprint.setVersion(blueprintDto.getVersion());
        }
        if (blueprintDto.getDisplayName() != null) {
            blueprint.setDisplayName(blueprintDto.getDisplayName());
        }
        if (blueprintDto.getCreatedDate() != null) {
            blueprint.setCreatedDate(blueprintDto.getCreatedDate());
        }
        if (blueprintDto.getLastModifiedDate() != null) {
            blueprint.setLastModifiedDate(blueprintDto.getLastModifiedDate());
        }
        if (blueprintDto.getIsDeleted() != null) {
            blueprint.setIsDeleted(blueprintDto.getIsDeleted());
        }
        if (blueprintDto.getDescription() != null) {
            blueprint.setDescription(blueprintDto.getDescription());
        }
        if (blueprintDto.getStampGrantCondDescription() != null) {
            blueprint.setStampGrantCondDescription(blueprintDto.getStampGrantCondDescription());
        }
        if (blueprintDto.getNumMaxStamps() != null) {
            blueprint.setNumMaxStamps(blueprintDto.getNumMaxStamps());
        }
        if (blueprintDto.getNumMaxRedeems() != null) {
            blueprint.setNumMaxRedeems(blueprintDto.getNumMaxRedeems());
        }
        if (blueprintDto.getNumMaxIssuesPerCustomer() != null) {
            blueprint.setNumMaxIssues(blueprintDto.getNumMaxIssuesPerCustomer());
        }
        if (blueprintDto.getNumMaxIssues() != null) {
            blueprint.setNumMaxIssues(blueprintDto.getNumMaxIssues());
        }
        if (blueprintDto.getExpirationDate() != null) {
            blueprint.setExpirationDate(blueprintDto.getExpirationDate());
        }
        if (blueprintDto.getBgImageId() != null) {
            blueprint.setBgImageId(blueprintDto.getBgImageId());
        }
        if (blueprintDto.getIsPublishing() != null) {
            blueprint.setIsPublishing(blueprintDto.getIsPublishing());
        }
        if (blueprintDto.getStoreId() != null) {
            blueprint.setStore(
                    storeRepository.getReferenceById(blueprintDto.getStoreId()));
        }
        if (blueprintDto.getRedeemRuleDtos() != null) {
            blueprint.setRedeemRules(blueprintDto.getRedeemRuleDtos().stream()
                    .map(redeemRuleMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return blueprint;
    }
}
