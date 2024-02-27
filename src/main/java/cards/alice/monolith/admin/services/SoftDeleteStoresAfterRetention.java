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
public class SoftDeleteStoresAfterRetention implements JobService {
    @Value("${cards.alice.admin.store.job.soft-delete-inactive-resources-after-retention.retention-in-days}")
    private long retentionInDays;

    private final AdminStoreRepository storeRepository;

    @Override
    @Transactional
    @Scheduled(
            zone = "${cards.alice.admin.store.job.soft-delete-resources-after-retention.zone}",
            cron = "${cards.alice.admin.store.job.soft-delete-resources-after-retention.cron}")
    public void run() {
        //TODO: restore
        final OffsetDateTime retentionExpirationDate = OffsetDateTime.now().minusDays(retentionInDays);

        storeRepository.updateIsDeletedByIsInactiveAndLastModifiedDateBefore(
                Boolean.TRUE, Boolean.TRUE, retentionExpirationDate);
    }
}
