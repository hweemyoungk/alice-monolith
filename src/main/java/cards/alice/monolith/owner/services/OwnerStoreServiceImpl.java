package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;

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

    @Override
    public StoreDto saveNewStore(StoreDto storeDto) {
        storeDto.setIdVersionNull();
        final Store store = storeMapper.toEntity(storeDto);
        return storeMapper.toDto(
                storeRepository.save(store));
    }

    @Override
    public Optional<StoreDto> getStoreById(Long id) {
        return Optional.ofNullable(storeMapper.toDto(
                storeRepository.findById(id).orElse(null)));
    }

    @Override
    public Optional<StoreDto> updateStoreById(Long id, StoreDto storeDto) {
        // Authenticate
        Optional<Store> store = storeRepository.findById(id);

        final var atomicReference = new AtomicReference<Optional<StoreDto>>();
        store.ifPresentOrElse(
                srcStore -> {
                    storeDto.setIdVersionNull();
                    storeMapper.partialUpdate(storeDto, srcStore);
                    final Store savedStore = storeRepository.save(srcStore);
                    final StoreDto savedStoreDto = storeMapper.toDto(savedStore);
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
