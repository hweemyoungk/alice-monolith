package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminStoreRepository extends StoreRepository {
    @Transactional
    @Modifying
    @Query("""
            update Store s set s.isDeleted = :isDeleted
            where s.isInactive = :isInactive and s.lastModifiedDate < :lastModifiedDate""")
    int updateIsDeletedByIsInactiveAndLastModifiedDateBefore(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("isInactive") Boolean isInactive, @NonNull @Param("lastModifiedDate") OffsetDateTime lastModifiedDate);

    @Transactional
    @Modifying
    @Query("delete from Store s where s.isDeleted = :isDeleted and s.lastModifiedDate < :lastModifiedDate")
    int deleteByIsDeletedAndLastModifiedDateBefore(@NonNull Boolean isDeleted, @NonNull OffsetDateTime lastModifiedDate);

    @Query("select s from Store s where s.isDeleted = :isDeleted and s.isInactive = :isInactive and s.ownerId = :ownerId")
    List<Store> findByIsDeletedAndIsInactiveAndOwnerId(@Param("isDeleted") @NonNull Boolean isDeleted, @Param("isInactive") @NonNull Boolean isInactive, @Param("ownerId") @NonNull UUID ownerId);

    @Transactional
    @Modifying
    @Query("update Store s set s.isDeleted = :isDeleted where s.ownerId = :ownerId")
    int updateIsDeletedByOwnerId(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("ownerId") UUID ownerId);
}
