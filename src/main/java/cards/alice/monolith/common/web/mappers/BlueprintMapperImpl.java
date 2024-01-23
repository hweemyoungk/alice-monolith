package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BlueprintMapperImpl implements BlueprintMapper {
    private final RedeemRuleMapper redeemRuleMapper;
    private final EntityManager entityManager;

    @Override
    public Blueprint toEntity(BlueprintDto blueprintDto) {
        if (blueprintDto == null) {
            return null;
        }

        Blueprint.BlueprintBuilder<?, ?> blueprint = Blueprint.builder()
                .id(blueprintDto.getId())
                .version(blueprintDto.getVersion())
                .displayName(blueprintDto.getDisplayName())
                .createdDate(blueprintDto.getCreatedDate())
                .lastModifiedDate(blueprintDto.getLastModifiedDate())
                .isDeleted(blueprintDto.getIsDeleted())
                .description(blueprintDto.getDescription())
                .stampGrantCondDescription(blueprintDto.getStampGrantCondDescription())
                .numMaxStamps(blueprintDto.getNumMaxStamps())
                .numMaxRedeems(blueprintDto.getNumMaxRedeems())
                .numMaxIssues(blueprintDto.getNumMaxIssues())
                .expirationDate(blueprintDto.getExpirationDate())
                .bgImageId(blueprintDto.getBgImageId())
                .isPublishing(blueprintDto.getIsPublishing())
                .redeemRules(blueprintDto.getRedeemRuleDtos().stream()
                        .map(redeemRuleDto -> {
                            redeemRuleDto.setVersion(null);
                            if (redeemRuleDto.getId() == null) {
                                // New redeemRule
                                return redeemRuleMapper.toEntity(redeemRuleDto);
                            }
                            // Modifying redeemRule
                            RedeemRule reference = entityManager.getReference(RedeemRule.class, redeemRuleDto.getId());
                            redeemRuleMapper.partialUpdate(redeemRuleDto, reference);
                            return reference;
                        })
                        .collect(Collectors.toSet()))
                .store(entityManager.getReference(Store.class, blueprintDto.getStoreId()));

        return blueprint.build();
    }

    @Override
    public BlueprintDto toDto(Blueprint blueprint) {
        if (blueprint == null) {
            return null;
        }

        BlueprintDto.BlueprintDtoBuilder<?, ?> blueprintDto = BlueprintDto.builder()
                .id(blueprint.getId())
                .version(blueprint.getVersion())
                .displayName(blueprint.getDisplayName())
                .createdDate(blueprint.getCreatedDate())
                .lastModifiedDate(blueprint.getLastModifiedDate())
                .isDeleted(blueprint.getIsDeleted())
                .description(blueprint.getDescription())
                .stampGrantCondDescription(blueprint.getStampGrantCondDescription())
                .numMaxStamps(blueprint.getNumMaxStamps())
                .numMaxRedeems(blueprint.getNumMaxRedeems())
                .numMaxIssues(blueprint.getNumMaxIssues())
                .expirationDate(blueprint.getExpirationDate())
                .bgImageId(blueprint.getBgImageId())
                .isPublishing(blueprint.getIsPublishing())
                .redeemRuleDtos(blueprint.getRedeemRules() == null ? null :
                        blueprint.getRedeemRules().stream()
                                .map(redeemRuleMapper::toDto)
                                .collect(Collectors.toSet()))
                .storeId(blueprint.getStore().getId());

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
            blueprint.setVersion(blueprintDto.getVersion());
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
        if (blueprintDto.getRedeemRuleDtos() != null) {
            blueprint.setRedeemRules(blueprintDto.getRedeemRuleDtos().stream()
                    .map(redeemRuleDto -> {
                        redeemRuleDto.setVersion(null);
                        if (redeemRuleDto.getId() == null) {
                            // New redeemRule
                            return redeemRuleMapper.toEntity(redeemRuleDto);
                        }
                        // Modifying redeemRule
                        RedeemRule reference = entityManager.getReference(RedeemRule.class, redeemRuleDto.getId());
                        redeemRuleMapper.partialUpdate(redeemRuleDto, reference);
                        return reference;
                    })
                    .collect(Collectors.toSet()));
        }
        if (blueprintDto.getStoreId() != null) {
            blueprint.setStore(
                    entityManager.getReference(Store.class, blueprintDto.getStoreId()));
        }

        return blueprint;
    }
}
