package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import cards.alice.monolith.owner.models.processors.OwnerStoreDtoProcessor;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerStoreServiceImpl implements OwnerStoreService {
    private final OwnerStoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final OwnerStoreDtoProcessor storeDtoProcessor;
    private final OwnerBlueprintService ownerBlueprintService;
    private final EntityManager em;

    @Override
    @Transactional
    public StoreDto saveNewStore(StoreDto storeDto) {
        final Set<BlueprintDto> blueprintDtos = storeDto.getBlueprintDtos();
        final StoreDto preprocessedForPost = storeDtoProcessor
                .preprocessForPostSingle(storeDto);

        // Save Store without Blueprints
        final Store store = storeMapper.toEntity(preprocessedForPost);
        final StoreDto savedStoreDto = storeMapper.toDto(
                storeRepository.save(store));

        if (blueprintDtos == null) {
            return savedStoreDto;
        }

        // Save Blueprints
        // 1. Set store id: Otherwise blueprintDtoProcessor.preprocessForPost will throw
        blueprintDtos.forEach(blueprintDto -> blueprintDto.setStoreId(savedStoreDto.getId()));

        // 2. Save
        final Set<BlueprintDto> savedBlueprintDtos = ownerBlueprintService.saveBlueprints(blueprintDtos);

        savedStoreDto.setBlueprintDtos(savedBlueprintDtos);
        return savedStoreDto;
    }

    @Override
    public Optional<StoreDto> getStoreById(Long id) {
        return Optional.ofNullable(storeMapper.toDto(
                storeRepository.findById(id).orElse(null)));
    }

    @Override
    @Transactional
    public Optional<StoreDto> updateStoreById(Long id, StoreDto storeDto) {
        StoreDto preprocessedForPut = storeDtoProcessor.preprocessForPut(id, storeDto);
        Store savedStore = storeRepository.save(storeMapper.toEntity(preprocessedForPut));
        if (Persistence.getPersistenceUtil().isLoaded(savedStore, "blueprints") && savedStore.getBlueprints() != null) {
            savedStore.getBlueprints().forEach(this::detachStore);
        }
        final StoreDto savedStoreDto = storeMapper.toDto(savedStore);
        return Optional.of(savedStoreDto);

    }

    @Override
    @Transactional
    public Optional<StoreDto> patchStoreById(Long id, StoreDto storeDto) {
        final Optional<Store> store = storeRepository.findById(id);
        final var atomicReference = new AtomicReference<Optional<StoreDto>>();
        store.ifPresentOrElse(
                srcStore -> {
                    em.detach(srcStore);
                    final Store patchedStore = storeMapper.partialUpdate(storeDto, srcStore);
                    patchedStore.setBlueprints(null); // I fucking don't know why store is eagerly fetching blueprints...
                    // final Store savedStore = storeRepository.save(patchedStore); // Neither fucking know why persists children...
                    // final StoreDto savedStoreDto = storeMapper.toDto(savedStore);
                    StoreDto patchedStoreDto = storeMapper.toDto(patchedStore);
                    var updatedStoreDto = updateStoreById(id, patchedStoreDto).orElseThrow();
                    atomicReference.set(Optional.of(updatedStoreDto));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                }
        );
        return atomicReference.get();
    }

    @Override
    @PostFilter("authentication.name == filterObject.ownerId.toString()")
    public Set<StoreDto> listStores(UUID ownerId, Set<Long> ids) {
        final Set<Store> stores;
        if (ids == null) {
            stores = storeRepository.findByOwnerId(ownerId);
        } else {
            stores = storeRepository.findByOwnerIdAndIdIn(ownerId, ids);
        }
        return stores.stream().peek(store -> store.getBlueprints().stream().peek(blueprint -> {
            blueprint.getRedeemRules().forEach(this::detachBlueprint);
        }).forEach(this::detachStore)).map(storeMapper::toDto).collect(Collectors.toSet());
        //return stores.stream()
        //        .map(storeMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public Optional<StoreDto> closeStoreById(Long id) {
        final StoreDto storeDto = StoreDto.builder()
                .isClosed(true)
                .isInactive(true)
                .build();
        return patchStoreById(id, storeDto);
    }

    @Override
    public Long getNumAccumulatedTotalStores(UUID ownerId) {
        return storeRepository.countByOwnerId(ownerId);
    }

    @Override
    public Long getNumCurrentTotalStores(UUID ownerId) {
        return storeRepository.countByIsDeletedAndOwnerId(Boolean.FALSE, ownerId);
    }

    @Override
    public Long getNumCurrentActiveStores(UUID ownerId) {
        return storeRepository.countByIsDeletedAndIsInactiveAndOwnerId(
                Boolean.FALSE, Boolean.FALSE, ownerId);
    }

    private void detachStore(Blueprint blueprint) {
        Store manyToOneStore = blueprint.getStore();
        em.detach(manyToOneStore);
        blueprint.setStore(em.getReference(Store.class, manyToOneStore.getId()));
    }

    private void detachBlueprint(RedeemRule redeemRule) {
        Blueprint manyToOneBlueprint = redeemRule.getBlueprint();
        em.detach(manyToOneBlueprint);
        redeemRule.setBlueprint(em.getReference(Blueprint.class, manyToOneBlueprint.getId()));
    }

    /*private void detachBlueprints(Store store) {
        final var oneToManyBlueprints = store.getBlueprints();
        final Set<Blueprint> blueprintReferences = oneToManyBlueprints.stream().map(blueprint -> {
            final Long blueprintId = blueprint.getId();
            em.detach(blueprint);
            return em.getReference(Blueprint.class, blueprintId);
        }).collect(Collectors.toSet());
        store.setBlueprints(blueprintReferences);
    }*/
}
