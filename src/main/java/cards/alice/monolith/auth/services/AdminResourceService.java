package cards.alice.monolith.auth.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AdminResourceService {
    @Transactional
    void softDeleteOwnerResources(UUID ownerId);

    @Transactional
    void softDeleteCustomerResources(UUID customerId);
}
