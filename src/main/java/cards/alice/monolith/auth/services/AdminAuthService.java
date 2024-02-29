package cards.alice.monolith.auth.services;

import java.util.UUID;

public interface AdminAuthService {
    void deleteKeycloakUser(UUID userId);
}
