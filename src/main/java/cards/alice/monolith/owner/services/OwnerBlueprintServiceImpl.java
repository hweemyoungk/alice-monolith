package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.web.mappers.BlueprintMapper;
import cards.alice.monolith.owner.models.processors.OwnerBlueprintDtoProcessor;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerBlueprintServiceImpl implements OwnerBlueprintService {
    private final OwnerStoreRepository storeRepository;
    private final OwnerBlueprintRepository blueprintRepository;
    private final BlueprintMapper blueprintMapper;
    private final OwnerBlueprintDtoProcessor blueprintDtoProcessor;
    private final OwnerRedeemRuleService ownerRedeemRuleService;

    // Tested
    @Override
    @Transactional
    public BlueprintDto saveNewBlueprint(BlueprintDto blueprintDto) {
        // Save blueprint without redeemRules
        final Set<RedeemRuleDto> redeemRuleDtos = blueprintDto.getRedeemRuleDtos();
        final BlueprintDto preprocessedBlueprintDto = blueprintDtoProcessor.preprocessForPost(blueprintDto);
        final Blueprint blueprint = blueprintMapper.toEntity(preprocessedBlueprintDto);
        final BlueprintDto savedBlueprintDto = blueprintMapper.toDto(
                blueprintRepository.save(blueprint));

        if (CollectionUtils.isEmpty(redeemRuleDtos)) {
            return savedBlueprintDto;
        }

        // Save redeemRules
        // 1. Set blueprint id: Otherwise redeemRuleDtoProcessor.preprocessForPost will throw
        redeemRuleDtos.forEach(redeemRuleDto -> redeemRuleDto.setBlueprintId(savedBlueprintDto.getId()));
        // Skip redeemRuleDtoProcessor.preprocessForPost: Will be done in ownerRedeemRuleService.saveRedeemRules.
        // 2. Save
        final Set<RedeemRuleDto> savedRedeemRuleDtos = ownerRedeemRuleService.saveRedeemRules(redeemRuleDtos);

        savedBlueprintDto.setRedeemRuleDtos(savedRedeemRuleDtos);
        return savedBlueprintDto;
    }

    // Tested
    @Override
    public Optional<BlueprintDto> getBlueprintById(Long id) {
        return Optional.ofNullable(blueprintMapper.toDto(
                blueprintRepository.findById(id).orElse(null)));

    }

    // Tested
    @Override
    @Transactional
    public Optional<BlueprintDto> updateBlueprintById(Long id, BlueprintDto blueprintDto) {
        // Update blueprint without redeemRules
        final Set<RedeemRuleDto> redeemRuleDtos = blueprintDto.getRedeemRuleDtos();

        final BlueprintDto preprocessedForPut = blueprintDtoProcessor
                .preprocessForPut(id, blueprintDto);
        final BlueprintDto savedBlueprintDto = blueprintMapper.toDto(blueprintRepository
                .save(blueprintMapper.toEntity(preprocessedForPut)));

        if (redeemRuleDtos == null) {
            return Optional.of(savedBlueprintDto);
        }

        // Save redeemRules
        // 1. Set blueprint id to new redeem rule, if any: redeemRuleDtoProcessor.preprocessForPost will throw
        redeemRuleDtos.forEach(redeemRuleDto -> redeemRuleDto.setBlueprintId(savedBlueprintDto.getId()));
        // Skip redeemRuleDtoProcessor.preprocessForPu: Will be done in ownerRedeemRuleService.saveRedeemRules.
        // 2. Save
        final Set<RedeemRuleDto> savedRedeemRuleDtos = ownerRedeemRuleService.saveRedeemRules(redeemRuleDtos);

        savedBlueprintDto.setRedeemRuleDtos(savedRedeemRuleDtos);
        return Optional.of(savedBlueprintDto);
    }

    @Override
    @Transactional
    public Optional<BlueprintDto> patchBlueprintById(Long id, BlueprintDto blueprintDto) {
        final Optional<Blueprint> blueprint = blueprintRepository.findById(id);
        final var atomicReference = new AtomicReference<Optional<BlueprintDto>>();
        blueprint.ifPresentOrElse(
                srcBlueprint -> {
                    blueprintMapper.partialUpdate(blueprintDto, srcBlueprint);
                    final Blueprint savedBlueprint = blueprintRepository.save(srcBlueprint);
                    final BlueprintDto savedBlueprintDto = blueprintMapper.toDto(savedBlueprint);
                    atomicReference.set(Optional.of(savedBlueprintDto));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                }
        );
        return atomicReference.get();
    }

    @Override
    public Set<BlueprintDto> listBlueprints(Long storeId, Set<Long> ids) {
        // Authenticate
        if (storeId != null) {
            storeRepository.findById(storeId);
        }

        final Set<Blueprint> blueprints;
        if (ids == null) {
            blueprints = blueprintRepository.findByStore_Id(storeId);
        } else {
            blueprints = blueprintRepository.findByStore_IdAndIdIn(storeId, ids);
        }
        return blueprints.stream()
                .map(blueprintMapper::toDto).collect(Collectors.toSet());
    }

    /**
     * @param blueprintDtos Could be mixture of old and brand new.
     */
    @Override
    public Set<BlueprintDto> saveBlueprints(Set<BlueprintDto> blueprintDtos) {
        return blueprintRepository.saveAll(blueprintDtos.stream()
                        .map(blueprintMapper::toEntity).collect(Collectors.toSet())).stream()
                .map(blueprintMapper::toDto).collect(Collectors.toSet());
    }
}
