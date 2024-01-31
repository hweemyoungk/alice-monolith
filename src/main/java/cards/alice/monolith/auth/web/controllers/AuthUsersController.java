package cards.alice.monolith.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${cards.athena.web.controllers.path.base}")
@RequiredArgsConstructor
public class AuthUsersController {
    @Value("${cards.alice.auth.realm-name}")
    private String realm;

    private final Keycloak keycloak;

    @PutMapping(path = "${cards.athena.web.controllers.path.users}/{userId}/groups/{groupId}")
    public ResponseEntity addUserToGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        keycloak.realm(realm).users().get(userId.toString()).joinGroup(groupId.toString());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "${cards.athena.web.controllers.path.users}/{userId}")
    public ResponseEntity softDeleteUser(@PathVariable UUID userId) {
        // 1. Soft delete (disable) user
        /*
        UserResource userResource = keycloak.realm(realm).users().get(userId.toString());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(Boolean.FALSE);
        userResource.update(userRepresentation);
        */
        // 2. Soft delete resources bound to user.
        // ...
        return null;
    }

    @PutMapping(path = "${cards.athena.web.controllers.path.users}/{userId}/groups/{groupId}")
    public ResponseEntity addUserToCustomerGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        // 1. Add to target customer group
        // ...
        // 2. Remove from any other customer group(s)
        // ...
        return null;
    }
}
