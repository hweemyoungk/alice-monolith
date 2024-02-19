package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Customer can only query Blueprint that is PUBLISHING and NOT DELETED.
 */
public interface CustomerBlueprintRepository extends BlueprintRepository {
    @Override
    @Query("""
            select b from Blueprint b
            where b.id = :id
            and b.isPublishing = true
            and b.isDeleted = false""")
    Optional<Blueprint> findById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select b from Blueprint b
            where b.id = :id
            and b.isPublishing = true
            and b.isDeleted = false""")
    Optional<Blueprint> exclusiveLockById(@Param("id") Long id);

    @Override
    @Query("""
            select b from Blueprint b
            where b.store.id = :id
            and b.isPublishing = true
            and b.isDeleted = false""")
    Set<Blueprint> findByStore_Id(Long id);

    @Override
    @Query("""
            select b from Blueprint b
            where (:storeId is null or b.store.id = :storeId)
            and b.id in :ids
            and b.isPublishing = true
            and b.isDeleted = false""")
    Set<Blueprint> findByStore_IdAndIdIn(@Nullable @Param("storeId") Long storeId, @NonNull @Param("ids") Collection<Long> ids);
}
