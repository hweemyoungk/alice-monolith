package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.StoreDto;

import java.util.Set;
import java.util.UUID;

public interface CustomerStoreService {
    Set<StoreDto> listStores(UUID ownerId, Set<Long> ids);
}
