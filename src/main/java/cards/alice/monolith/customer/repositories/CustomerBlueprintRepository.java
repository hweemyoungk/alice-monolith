package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface CustomerBlueprintRepository extends BlueprintRepository {
    @Override
    @PostAuthorize("returnObject.isEmpty() ? true : returnObject.get().isPublishing")
    Optional<Blueprint> findById(Long aLong);
}
