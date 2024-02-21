package cards.alice.monolith.auth.web.controllers;

import cards.alice.monolith.auth.services.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${cards.alice.auth.web.controllers.path.base}")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserAuthService userAuthService;

    @DeleteMapping(path = "${cards.alice.auth.web.controllers.path.user}/{userId}")
    public ResponseEntity<Void> softDeleteUser(@PathVariable UUID userId) {
        userAuthService.softDeleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    // Changing membership is not supported yet.
    /*
    @PutMapping(path = "${cards.athena.web.controllers.path.users}/{userId}/groups/{groupId}")
    public ResponseEntity addUserToGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        keycloak.realm(realm).users().get(userId.toString()).joinGroup(groupId.toString());
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "${cards.athena.web.controllers.path.users}/{userId}/groups/{groupId}")
    public ResponseEntity addUserToCustomerGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        // 1. Add to target customer group
        // ...
        // 2. Remove from any other customer group(s)
        // ...
        return null;
    }
    */
}
