package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerStoreServiceImpl implements OwnerStoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
    public StoreDto saveNewStore(StoreDto storeDto) {
        final Store store = storeMapper.toEntity(storeDto);
        store.setId(null);
        store.setVersion(null);
        return storeMapper.toDto(
                storeRepository.save(store));
    }

    @Override
    public Optional<StoreDto> getStoreById(Long id) {
        return Optional.ofNullable(
                storeMapper.toDto(
                        storeRepository.findById(id).orElse(null)));
    }

    @Override
    public Optional<StoreDto> updateStoreById(Long id, StoreDto storeDto) {
        final var atomicReference = new AtomicReference<Optional<StoreDto>>();
        storeRepository.findById(id).ifPresentOrElse(
                srcStore -> {
                    final Store destStore = storeMapper.toEntity(storeDto);
                    destStore.setId(srcStore.getId());
                    destStore.setVersion(srcStore.getVersion());
                    final Store savedStore = storeRepository.save(destStore);
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
    public Set<StoreDto> listStores(UUID ownerId, Set<Long> ids) {
        return storeRepository.findByOwnerIdAndIdIn(ownerId, ids).stream()
                .map(storeMapper::toDto).collect(Collectors.toSet());
    }
}
