package cards.alice.monolith.common.config;

import java.util.Optional;

public interface AuthenticatedEntityAccessor<T, ID> {
    Optional<T> findById(ID id);
}
