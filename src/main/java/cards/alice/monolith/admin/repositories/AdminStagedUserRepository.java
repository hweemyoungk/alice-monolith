package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.StagedUser;
import cards.alice.monolith.common.repositories.StagedUserRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminStagedUserRepository extends StagedUserRepository {
    @Transactional
    @Modifying
    @Query("delete from StagedUser s where s.userId in :userIds")
    int deleteByUserIdIn(@Param("userIds") @NonNull Collection<UUID> userIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StagedUser s where s.isDeleted = :isDeleted and s.lastModifiedDate < :lastModifiedDate")
    List<StagedUser> exclusiveFindByIsDeletedAndLastModifiedDateBefore(@Param("isDeleted") @NonNull Boolean isDeleted, @Param("lastModifiedDate") @NonNull OffsetDateTime lastModifiedDate);

    @Transactional
    @Modifying
    @Query("delete from StagedUser s where s.isDeleted = :isDeleted and s.lastModifiedDate < :lastModifiedDate")
    int deleteByIsDeletedAndLastModifiedDateBefore(@NonNull Boolean isDeleted, @NonNull OffsetDateTime lastModifiedDate);

    @Query("select s from StagedUser s where s.userId = :userId and (:isDeleted is null or s.isDeleted = :isDeleted)")
    Optional<StagedUser> findByUserIdAndIsDeleted(@Param("userId") @NonNull UUID userId, @Param("isDeleted") @Nullable Boolean isDeleted);
}
