package cards.alice.monolith.common.config;

public interface AuthenticatedEntityAccessor<T, ID> {
    T authenticatedGetById(ID id);
}
