package cards.alice.monolith.auth.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserAuthService {
    @Transactional
    void softDeleteUserById(UUID userId);

    void disableKeycloakUser(UUID userId);
}
