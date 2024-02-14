package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OwnerStoreRepository extends StoreRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    Optional<Store> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"blueprints.redeemRules"})
    //@EntityGraph("owner-models")
    //@Query("select s from Store s left join fetch Blueprint b on b.store.id = s.id left join fetch RedeemRule rr on rr.blueprint.id = b.id where s.ownerId = :ownerId")
    Set<Store> findByOwnerId(UUID ownerId);

    @Override
    @PreAuthorize("authentication.name == #entity.ownerId.toString()")
    <S extends Store> S save(S entity);
}
