package cards.alice.monolith.auth.services;

import cards.alice.monolith.admin.repositories.AdminBlueprintRepository;
import cards.alice.monolith.admin.repositories.AdminStagedUserRepository;
import cards.alice.monolith.admin.repositories.AdminStoreRepository;
import cards.alice.monolith.common.domain.BaseEntity;
import cards.alice.monolith.common.domain.StagedUser;
import cards.alice.monolith.common.web.exceptions.CustomValidationException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    @Value("${cards.alice.auth.realm-name}")
    private String realm;
    private final Keycloak keycloak;

    private final AdminStoreRepository adminStoreRepository;
    private final AdminBlueprintRepository adminBlueprintRepository;
    private final AdminStagedUserRepository adminStagedUserRepository;

    private final AdminResourceService adminResourceService;

    @Override
    @Transactional
    public void softDeleteUserById(UUID userId) {
        // 0. Validate
        validateSoftDeleteUser(userId);

        // 1. Save StagedUser
        adminStagedUserRepository.findByUserIdAndIsDeleted(userId, null).ifPresentOrElse(stagedUser -> {
            stagedUser.setIsDeleted(Boolean.TRUE);
            adminStagedUserRepository.save(stagedUser);
        }, () -> {
            var softDeletedUser = StagedUser.builder()
                    .displayName("User staged for soft-delete")
                    .isDeleted(Boolean.TRUE)
                    .userId(userId)
                    .build();
            adminStagedUserRepository.save(softDeletedUser);
        });

        // 2. Soft-delete resources
        softDeleteUserResources(userId);

        // 3. Soft-delete(disable) user
        disableKeycloakUser(userId);
    }

    @Override
    public void disableKeycloakUser(UUID userId) {
        UserResource userResource = keycloak.realm(realm).users().get(userId.toString());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(Boolean.FALSE);
        userResource.update(userRepresentation);
    }

    private void softDeleteUserResources(UUID userId) {
        // As owner
        adminResourceService.softDeleteOwnerResources(userId);

        // As customer
        adminResourceService.softDeleteCustomerResources(userId);
    }

    private void validateSoftDeleteUser(UUID userId) {
        // 0. userId must match current user
        if (!userId.toString().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            throw new AuthenticationServiceException("Current username doesn't match provided userId");
        }

        final List<String> violationMessages = new ArrayList<>();

        // TODO: Do we really need this?
        // 1. Check user has already been staged.
        /*adminStagedUserRepository.findByUserIdAndIsDeleted(userId, Boolean.TRUE).ifPresent(
                stagedUser -> {
                    violationMessages.add("User already staged for delete");
                }
        );*/

        // 2. As owner
        violationMessages.addAll(validateSoftDeleteOwner(userId));

        // 3. As customer
        violationMessages.addAll(validateSoftDeleteCustomer(userId));

        if (!violationMessages.isEmpty()) {
            throw new CustomValidationException("Failed to validate user for soft-delete: " + violationMessages);
        }
    }

    private List<String> validateSoftDeleteOwner(UUID ownerId) {
        final List<String> violationMessages = new ArrayList<>();

        // 1. Store
        // Every store must be inactive (closed).
        // Querying illegal rows, so lock is not needed.
        var activeStores = adminStoreRepository
                .findByIsDeletedAndIsInactiveAndOwnerId(Boolean.FALSE, Boolean.FALSE, ownerId);
        if (!activeStores.isEmpty()) {
            violationMessages.add("Owner owns active stores: " + activeStores.stream().map(BaseEntity::getDisplayName).toList());
        }

        // 2. Blueprint
        // Every blueprint must be inactive (expired).
        // Querying illegal rows, so lock is not needed.
        // Ignored: better proceed with active blueprints when there's no active stores...
        /*
        var activeBlueprints = adminBlueprintRepository.findByIsDeletedAndExpirationDateAfterAndStore_OwnerId(
                Boolean.FALSE, OffsetDateTime.now(), ownerId);
        if (!activeBlueprints.isEmpty()) {
            violationMessages.add("Owner owns active blueprints: " + activeBlueprints.stream()
                    .map(blueprint -> blueprint.getDisplayName() + " of store " + blueprint.getStore().getDisplayName()).toList());
        }
        */

        // 3. RedeemRule
        // NO-OP

        // 4. Card
        // Not relevant

        // 5. Redeem
        // Not relevant

        // 6. StampGrant
        // Not relevant

        return violationMessages;
    }

    private List<String> validateSoftDeleteCustomer(UUID customerId) {
        // Customer is always ready to quit and not responsible for resources.
        // 1. Card
        // NO-OP
        // 2. Redeem
        // NO-OP
        // 3. StampGrant
        // NO-OP
        // 4. Blueprint
        // NO-OP
        // 5. RedeemRule
        // NO-OP
        // 6. Store
        // NO-OP
        return new ArrayList<>();
    }
}
