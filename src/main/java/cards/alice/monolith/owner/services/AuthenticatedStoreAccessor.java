package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedStoreAccessor implements AuthenticatedEntityAccessor<Store, Long> {
    private final StoreRepository storeRepository;
    @Override
    @PostAuthorize("authentication.name == returnObject.ownerId.toString()")
    public Store authenticatedGetById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }
}
