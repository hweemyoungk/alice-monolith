package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerAuthenticatedStoreAccessor implements AuthenticatedEntityAccessor<Store, Long> {
    private final StoreRepository storeRepository;

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    public Optional<Store> findById(Long id) {
        return storeRepository.findById(id);
    }
}
