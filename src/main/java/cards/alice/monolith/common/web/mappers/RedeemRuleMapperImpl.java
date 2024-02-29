package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RedeemRuleMapperImpl implements RedeemRuleMapper {
    private final BlueprintRepository blueprintRepository;
    private final BlueprintMapper blueprintMapper;
    private final RedeemMapper redeemMapper;
    private final EntityManager entityManager;

    public RedeemRuleMapperImpl(BlueprintRepository blueprintRepository, BlueprintMapper blueprintMapper, @Lazy RedeemMapper redeemMapper, EntityManager entityManager) {
        this.blueprintRepository = blueprintRepository;
        this.blueprintMapper = blueprintMapper;
        this.redeemMapper = redeemMapper;
        this.entityManager = entityManager;
    }

    @Override
    public RedeemRule toEntity(RedeemRuleDto redeemRuleDto) {
        if (redeemRuleDto == null) {
            return null;
        }

        RedeemRule.RedeemRuleBuilder<?, ?> redeemRule = RedeemRule.builder()
                .id(redeemRuleDto.getId())
                //.version(redeemRuleDto.getVersion())
                .displayName(redeemRuleDto.getDisplayName())
                .createdDate(redeemRuleDto.getCreatedDate())
                .lastModifiedDate(redeemRuleDto.getLastModifiedDate())
                .isDeleted(redeemRuleDto.getIsDeleted())
                .description(redeemRuleDto.getDescription())
                .consumes(redeemRuleDto.getConsumes())
                .imageId(redeemRuleDto.getImageId())
                .blueprint(blueprintRepository.getReferenceById(redeemRuleDto.getBlueprintId()))
                .redeems(redeemRuleDto.getRedeemDtos() == null ? null :
                        redeemRuleDto.getRedeemDtos().stream()
                                .map(redeemMapper::toEntity)
                                .collect(Collectors.toSet()));

        return redeemRule.build();
    }

    @Override
    public RedeemRuleDto toDto(RedeemRule redeemRule) {
        if (redeemRule == null) {
            return null;
        }

        RedeemRuleDto.RedeemRuleDtoBuilder<?, ?> redeemRuleDto = RedeemRuleDto.builder()
                .id(redeemRule.getId())
                //.version(redeemRule.getVersion())
                .displayName(redeemRule.getDisplayName())
                .createdDate(redeemRule.getCreatedDate())
                .lastModifiedDate(redeemRule.getLastModifiedDate())
                .isDeleted(redeemRule.getIsDeleted())
                .description(redeemRule.getDescription())
                .consumes(redeemRule.getConsumes())
                .imageId(redeemRule.getImageId())
                .blueprintDto(!PERSISTENCE_UTIL.isLoaded(redeemRule, "blueprint") ?
                        null :
                        blueprintMapper.toDto(redeemRule.getBlueprint()))
                .blueprintId(redeemRule.getBlueprint() == null ? null : redeemRule.getBlueprint().getId())
                .redeemDtos(!PERSISTENCE_UTIL.isLoaded(redeemRule, "redeems") || redeemRule.getRedeems() == null ?
                        null :
                        redeemRule.getRedeems().stream()
                                .map(redeemMapper::toDto)
                                .collect(Collectors.toSet()));

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
            //redeemRule.setVersion(redeemRuleDto.getVersion());
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
        if (redeemRuleDto.getRedeemDtos() != null) {
            redeemRule.setRedeems(redeemRuleDto.getRedeemDtos().stream()
                    .map(redeemMapper::toEntity)
                    .collect(Collectors.toSet()));
        }

        return redeemRule;
    }
}
