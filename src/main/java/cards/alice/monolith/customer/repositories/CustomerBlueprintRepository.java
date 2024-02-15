package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface CustomerBlueprintRepository extends BlueprintRepository {
    /**
     * Finds blueprint that is PUBLISHING.
     * @param id must not be {@literal null}.
     */
    @Override
    //@PostAuthorize("returnObject.isEmpty() ? true : returnObject.get().isPublishing")
    @PostAuthorize("returnObject.isEmpty() ? true : (returnObject.get().isPublishing and !returnObject.get().isDeleted)")
    Optional<Blueprint> findById(Long id);
}
