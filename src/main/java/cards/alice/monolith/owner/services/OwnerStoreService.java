package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.StoreDto;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OwnerStoreService {
    StoreDto saveNewStore(StoreDto storeDto);

    Optional<StoreDto> getStoreById(Long id);

    Optional<StoreDto> updateStoreById(Long id, StoreDto storeDto);

    Optional<StoreDto> patchStoreById(Long id, StoreDto storeDto);

    Set<StoreDto> listStores(UUID ownerId, Set<Long> ids);

    Optional<StoreDto> closeStoreById(Long id);
}
