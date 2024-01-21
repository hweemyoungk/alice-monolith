package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.web.mappers.BlueprintMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerBlueprintServiceImpl implements OwnerBlueprintService {
    private final EntityManager entityManager;
    private final BlueprintRepository blueprintRepository;
    private final BlueprintMapper blueprintMapper;

    private void setStoreReference(Blueprint blueprint, Long id) {
        blueprint.setStore(entityManager.getReference(Store.class, id));
    }

    @Override
    public BlueprintDto saveNewBlueprint(BlueprintDto blueprintDto) {
        final Blueprint blueprint = blueprintMapper.toEntity(blueprintDto);
        blueprint.setId(null);
        blueprint.setVersion(null);
        return blueprintMapper.toDto(
                blueprintRepository.save(blueprint));
    }

    @Override
    public Optional<BlueprintDto> getBlueprintById(Long id) {
        return Optional.ofNullable(
                blueprintMapper.toDto(
                        blueprintRepository.findById(id).orElse(null)));

    }

    @Override
    public Optional<BlueprintDto> updateBlueprintById(Long id, BlueprintDto blueprintDto) {
        final var atomicReference = new AtomicReference<Optional<BlueprintDto>>();
        blueprintRepository.findById(id).ifPresentOrElse(
                srcBlueprint -> {
                    final Blueprint destBlueprint = blueprintMapper.toEntity(blueprintDto);
                    destBlueprint.setId(srcBlueprint.getId());
                    destBlueprint.setVersion(srcBlueprint.getVersion());
                    setStoreReference(destBlueprint, srcBlueprint.getId());
                    final Blueprint savedBlueprint = blueprintRepository.save(destBlueprint);
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
