package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("select s from Store s where s.ownerId = :ownerId")
    Set<Store> findByOwnerId(@Param("ownerId") @NonNull UUID ownerId);

    @Query("""
            select s from Store s
            where (:ownerId is null or s.ownerId = :ownerId)
            and s.id in :ids""")
    Set<Store> findByOwnerIdAndIdIn(@Param("ownerId") @Nullable UUID ownerId, @Param("ids") @NonNull Collection<Long> ids);

    @Query(value = """
            select count(0) from (
            select 0 from store as s
            where s.owner_id = :ownerId
            for update) as sq""",
            nativeQuery = true)
    long exclusiveCountByOwnerId(@Param("ownerId") @NonNull UUID ownerId);

    @Query(value = """
            select count(0) from (
            select 0 from store as s
            where s.owner_id = :ownerId
            and s.is_deleted = :isDeleted
            for update) as sq""",
            nativeQuery = true)
    long exclusiveCountByOwnerIdAndIsDeleted(@Param("ownerId") @NonNull UUID ownerId, @Param("isDeleted") @NonNull Boolean isDeleted);

    @Query(value = """
            select count(0) from (
            select 0 from store as s
            where s.owner_id = :ownerId
            and s.is_deleted = :isDeleted
            and s.is_inactive = :isInactive
            for update) as sq""",
            nativeQuery = true)
    long exclusiveCountByOwnerIdAndIsDeletedAndIsInactive(@Param("ownerId") @NonNull UUID ownerId, @Param("isDeleted") @NonNull Boolean isDeleted, @Param("isInactive") @NonNull Boolean isInactive);
}
