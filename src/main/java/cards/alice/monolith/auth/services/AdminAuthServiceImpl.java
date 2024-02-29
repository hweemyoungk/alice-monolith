package cards.alice.monolith.auth.services;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {
    @Value("${cards.alice.auth.realm-name}")
    private String realm;
    private final Keycloak keycloak;

    @Override
    public void deleteKeycloakUser(UUID userId) {
        try (Response response = keycloak.realm(realm).users().delete(userId.toString())) {
            HttpStatusCode statusCode = HttpStatusCode.valueOf(response.getStatus());
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.CLIENT_ERROR)) {
                throw new HttpClientErrorException(statusCode, "Failed to delete keycloak user: " + userId);
            }
            if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SERVER_ERROR)) {
                throw new HttpServerErrorException(statusCode, "Failed to delete keycloak user: " + userId);
            }
        }
    }
}
