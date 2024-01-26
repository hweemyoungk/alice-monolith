package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface OwnerBlueprintRepository extends BlueprintRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().store.ownerId.toString()")
    Optional<Blueprint> findById(Long aLong);
}
