package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.StoreDto;

import java.util.Set;

public interface CustomerStoreService {
    Set<StoreDto> listStores(Set<Long> ids);
}
