package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AdminBlueprintRepository extends BlueprintRepository {
    @Transactional
    @Modifying
    @Query("delete from Blueprint b where b.isDeleted = :isDeleted and b.lastModifiedDate < :lastModifiedDate")
    int deleteByIsDeletedAndLastModifiedDateBefore(@NonNull Boolean isDeleted, @NonNull OffsetDateTime lastModifiedDate);

    @Query("""
            select b from Blueprint b
            where b.isDeleted = :isDeleted
            and b.expirationDate > :expirationDate
            and b.store.ownerId = :ownerId""")
    List<Blueprint> findByIsDeletedAndExpirationDateAfterAndStore_OwnerId(@Param("isDeleted") @NonNull Boolean isDeleted, @Param("expirationDate") @NonNull OffsetDateTime expirationDate, @Param("ownerId") @NonNull UUID ownerId);

    @Transactional
    @Modifying
    @Query("""
            update Blueprint b set b.isDeleted = :isDeleted
            where b.store.id in (
            select s.id from Store s where s.ownerId = :ownerId)""")
    int updateIsDeletedByStore_OwnerId(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("ownerId") UUID ownerId);
}
