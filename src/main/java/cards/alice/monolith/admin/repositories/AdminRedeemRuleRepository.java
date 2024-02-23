package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AdminRedeemRuleRepository extends RedeemRuleRepository {
    @Transactional
    @Modifying
    @Query("delete from RedeemRule r where r.isDeleted = :isDeleted and r.lastModifiedDate < :lastModifiedDate")
    int deleteByIsDeletedAndLastModifiedDateBefore(@NonNull Boolean isDeleted, @NonNull OffsetDateTime lastModifiedDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    @Modifying
    @Query("""
            update RedeemRule r set r.isDeleted = :isDeleted
            where r.blueprint.store.id in (
            select s.id from Store s where s.ownerId = :ownerId)""")
    int exclusiveUpdateIsDeletedByBlueprint_Store_OwnerId(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("ownerId") UUID ownerId);
}
