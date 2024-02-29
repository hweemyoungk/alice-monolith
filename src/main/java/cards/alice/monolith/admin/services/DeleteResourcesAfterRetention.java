package cards.alice.monolith.admin.services;

import cards.alice.monolith.admin.repositories.*;
import cards.alice.monolith.auth.services.AdminAuthService;
import cards.alice.monolith.common.domain.StagedUser;
import cards.alice.monolith.common.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeleteResourcesAfterRetention implements JobService {
    @Value("${cards.alice.admin.resource.job.delete-resources-after-retention.retention-in-seconds}")
    private long retentionInSeconds;

    private final AdminStoreRepository storeRepository;
    private final AdminBlueprintRepository blueprintRepository;
    private final AdminRedeemRuleRepository redeemRuleRepository;
    private final AdminCardRepository cardRepository;
    private final AdminStagedUserRepository stagedUserRepository;
    private final AdminRedeemRepository redeemRepository;
    private final AdminStampGrantRepository stampGrantRepository;

    private final AdminAuthService authService;

    @Override
    @Transactional
    @Scheduled(
            zone = "${cards.alice.admin.resource.job.delete-resources-after-retention.zone}",
            cron = "${cards.alice.admin.resource.job.delete-resources-after-retention.cron}")
    public void run() {
        final OffsetDateTime retentionExpirationDate = OffsetDateTime.now().minusSeconds(retentionInSeconds);

        // 1. Delete direct resources (and cascade set null).
        storeRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        blueprintRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        redeemRuleRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        cardRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);

        // 2. Delete indirect resources (orphans).
        blueprintRepository.deleteByStoreIsNull();
        redeemRuleRepository.deleteByBlueprintIsNull();
        cardRepository.deleteByBlueprintIsNull();
        redeemRepository.deleteByRedeemRuleIsNullAndCardIsNull();
        stampGrantRepository.deleteByCardIsNull();

        // 3. Delete StagedUser
        List<StagedUser> softDeletedUsers = stagedUserRepository.exclusiveFindByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        var userIds = softDeletedUsers.stream().map(stagedUser -> {
            authService.deleteKeycloakUser(stagedUser.getUserId());
            return stagedUser.getUserId();
        }).toList();
        stagedUserRepository.deleteByUserIdIn(userIds);
    }
}
