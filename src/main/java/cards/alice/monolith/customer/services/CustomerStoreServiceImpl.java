package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerStoreServiceImpl implements CustomerStoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    @Override
    public Set<StoreDto> listStores(Set<Long> ids) {
        return storeRepository.findByIdIn(ids).stream()
                .map(storeMapper::toDto).collect(Collectors.toSet());
    }
}
