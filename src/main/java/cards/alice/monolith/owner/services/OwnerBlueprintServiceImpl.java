package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.web.mappers.BlueprintMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerBlueprintServiceImpl implements OwnerBlueprintService {
    private final BlueprintRepository blueprintRepository;
    private final BlueprintMapper blueprintMapper;
    private final OwnerRedeemRuleService ownerRedeemRuleService;

    // Tested
    @Override
    @Transactional
    public BlueprintDto saveNewBlueprint(BlueprintDto blueprintDto) {
        final Blueprint blueprint = blueprintMapper.toEntity(blueprintDto);

        // Save blueprint without redeemRules
        blueprint.setRedeemRules(new HashSet<>());
        blueprint.setId(null);
        blueprint.setVersion(null);
        final BlueprintDto savedBlueprintDto = blueprintMapper.toDto(
                blueprintRepository.save(blueprint));

        // Save redeemRules
        final Set<RedeemRuleDto> redeemRuleDtos = blueprintDto.getRedeemRuleDtos();
        redeemRuleDtos.forEach(redeemRuleDto -> redeemRuleDto.setBlueprintId(savedBlueprintDto.getId()));
        final Set<RedeemRuleDto> savedRedeemRuleDtos = ownerRedeemRuleService.saveRedeemRules(redeemRuleDtos);

        savedBlueprintDto.setRedeemRuleDtos(savedRedeemRuleDtos);
        return savedBlueprintDto;
    }

    // Tested
    @Override
    public Optional<BlueprintDto> getBlueprintById(Long id) {
        return Optional.ofNullable(
                blueprintMapper.toDto(
                        blueprintRepository.findById(id).orElse(null)));

    }

    // Tested
    @Override
    public Optional<BlueprintDto> updateBlueprintById(Long id, BlueprintDto blueprintDto) {
        final var atomicReference = new AtomicReference<Optional<BlueprintDto>>();
        blueprintRepository.findById(id).ifPresentOrElse(
                srcBlueprint -> {
                    //final Blueprint destBlueprint = blueprintMapper.toEntity(blueprintDto);
                    //destBlueprint.setId(srcBlueprint.getId());
                    //destBlueprint.setVersion(srcBlueprint.getVersion());
                    //setStoreReference(destBlueprint, srcBlueprint.getStore().getId());
                    //final Blueprint savedBlueprint = blueprintRepository.save(destBlueprint);
                    blueprintDto.setId(null);
                    blueprintDto.setVersion(null);
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
        return blueprintRepository.findByStore_IdAndIdIn(storeId, ids).stream()
                .map(blueprintMapper::toDto).collect(Collectors.toSet());
    }
}
