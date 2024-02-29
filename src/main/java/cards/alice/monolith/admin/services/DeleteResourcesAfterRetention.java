package cards.alice.monolith.admin.services;

import cards.alice.monolith.admin.repositories.*;
import cards.alice.monolith.common.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

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

    @Override
    @Transactional
    @Scheduled(
            zone = "${cards.alice.admin.resource.job.delete-resources-after-retention.zone}",
            cron = "${cards.alice.admin.resource.job.delete-resources-after-retention.cron}")
    public void run() {
        final OffsetDateTime retentionExpirationDate = OffsetDateTime.now().minusSeconds(retentionInSeconds);

        // 1. Delete StagedUser (and make relevant resource's userId null).
        stagedUserRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);

        // 2. Delete direct resources (and cascade set null).
        storeRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        blueprintRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        redeemRuleRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);
        cardRepository.deleteByIsDeletedAndLastModifiedDateBefore(
                Boolean.TRUE, retentionExpirationDate);

        // 3. Delete indirect resources (orphans).
        redeemRepository.deleteByRedeemRuleAndCard(null, null);
        stampGrantRepository.deleteByCard(null);
    }
}
