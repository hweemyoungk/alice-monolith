package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long> {
    @Query("select b from Blueprint b where b.store.id = :id")
    Set<Blueprint> findByStore_Id(@NonNull @Param("id") Long id);

    @Query("select b from Blueprint b where (:storeId is null or b.store.id = :storeId) and b.id in :ids")
    Set<Blueprint> findByStore_IdAndIdIn(@Nullable @Param("storeId") Long storeId, @NonNull @Param("ids") Collection<Long> ids);

    /*@Query("""
            select count(b) from Blueprint b
            where b.store.id = :id
            and b.store.ownerId = :ownerId
            and b.isDeleted = :isDeleted""")*/
    @Query(value = """
            select count(0) from (
            select 0 from blueprint as b
            left join store as s on b.store_id = s.id
            where s.id = :id
            and s.owner_id = :ownerId
            and b.is_deleted = :isDeleted
            for update)""",
            nativeQuery = true)
    long exclusiveCountByStore_IdAndStore_OwnerIdAndIsDeleted(@Param("id") @NonNull Long id, @Param("ownerId") @NonNull UUID ownerId, @Param("isDeleted") @NonNull Boolean isDeleted);

    /*@Query("""
            select count(b) from Blueprint b
            where b.store.id = :id
            and b.store.ownerId = :ownerId
            and b.isDeleted = :isDeleted
            and b.isPublishing = :isPublishing""")*/
    @Query(value = """
            select count(0) from (
            select 0 from blueprint as b
            left join store as s on b.store_id = s.id
            where s.id = :id
            and s.owner_id = :ownerId
            and b.is_deleted = :isDeleted
            and b.is_publishing = :isPublishing
            for update)""",
            nativeQuery = true)
    long exclusiveCountByStore_IdAndStore_OwnerIdAndIsDeletedAndIsPublishing(@Param("id") @NonNull Long id, @Param("ownerId") @NonNull UUID ownerId, @Param("isDeleted") @NonNull Boolean isDeleted, @Param("isPublishing") @NonNull Boolean isPublishing);
}
