package cards.alice.monolith.auth.services;

import cards.alice.monolith.admin.repositories.AdminBlueprintRepository;
import cards.alice.monolith.admin.repositories.AdminCardRepository;
import cards.alice.monolith.admin.repositories.AdminRedeemRuleRepository;
import cards.alice.monolith.admin.repositories.AdminStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminResourceServiceImpl implements AdminResourceService {
    private final AdminStoreRepository adminStoreRepository;
    private final AdminBlueprintRepository adminBlueprintRepository;
    private final AdminRedeemRuleRepository adminRedeemRuleRepository;
    private final AdminCardRepository adminCardRepository;

    @Override
    @Transactional
    public void softDeleteOwnerResources(UUID ownerId) {
        // 1. Store
        adminStoreRepository.exclusiveUpdateIsDeletedByOwnerId(Boolean.TRUE, ownerId);

        // 2. Blueprint
        adminBlueprintRepository.exclusiveUpdateIsDeletedByStore_OwnerId(Boolean.TRUE, ownerId);

        // 3. RedeemRule
        adminRedeemRuleRepository.exclusiveUpdateIsDeletedByBlueprint_Store_OwnerId(Boolean.TRUE, ownerId);

        // 4. Card
        // NO-OP
        // 5. Redeem
        // NO-OP
        // 6. StampGrant
        // NO-OP
    }

    @Override
    @Transactional
    public void softDeleteCustomerResources(UUID customerId) {
        // 1. Card
        adminCardRepository.exclusiveUpdateIsDeletedByCustomerId(Boolean.TRUE, customerId);

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
    }
}
