package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import cards.alice.monolith.owner.models.processors.OwnerStoreDtoProcessor;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerStoreServiceImpl implements OwnerStoreService {
    private final OwnerStoreRepository storeRepository;
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerStoreDtoProcessor storeDtoProcessor;
    private final StoreMapper storeMapper;
    private final OwnerBlueprintService ownerBlueprintService;

    @Override
    @Transactional
    public StoreDto saveNewStore(StoreDto storeDto) {
        final StoreDto preprocessedStoreDto = storeDtoProcessor.preprocessForPost(storeDto);

        // Save Store without Blueprints
        final Store store = storeMapper.toEntity(preprocessedStoreDto);
        store.setBlueprints(new HashSet<>());
        final StoreDto savedStoreDto = storeMapper.toDto(
                storeRepository.save(store));

        if (preprocessedStoreDto.getBlueprintDtos() == null) {
            return savedStoreDto;
        }

        // Save Blueprints
        final Set<BlueprintDto> blueprintDtos = preprocessedStoreDto.getBlueprintDtos();
        blueprintDtos.forEach(blueprintDto -> blueprintDto.setStoreId(savedStoreDto.getId()));
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
        StoreDto preprocessedStoreDto = storeDtoProcessor.preprocessForPut(id, storeDto);
        return patchStoreById(id, preprocessedStoreDto);
    }

    @Override
    @Transactional
    public Optional<StoreDto> patchStoreById(Long id, StoreDto storeDto) {
        final Optional<Store> store = storeRepository.findById(id);
        final var atomicReference = new AtomicReference<Optional<StoreDto>>();
        store.ifPresentOrElse(
                srcStore -> {
                    storeMapper.partialUpdate(storeDto, srcStore);
                    final StoreDto savedStoreDto = storeMapper.toDto(
                            storeRepository.save(srcStore));
                    atomicReference.set(Optional.of(savedStoreDto));
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
        return stores.stream()
                .map(storeMapper::toDto).collect(Collectors.toSet());
    }
}
