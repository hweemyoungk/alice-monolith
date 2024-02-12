package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStoreServiceImpl implements CustomerStoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
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

    @Override
    public Optional<StoreDto> getStoreById(Long id) {
        return Optional.ofNullable(storeMapper.toDto(
                storeRepository.findById(id).orElse(null)));
    }
}
