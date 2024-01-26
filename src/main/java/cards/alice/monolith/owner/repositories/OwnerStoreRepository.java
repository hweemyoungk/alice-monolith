package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface OwnerStoreRepository extends StoreRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    Optional<Store> findById(Long aLong);
}
