package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("select s from Store s where s.ownerId = :ownerId")
    Set<Store> findByOwnerId(@Param("ownerId") UUID ownerId);
    @Query("select s from Store s where (:ownerId is null or s.ownerId = :ownerId) and s.id in :ids")
    Set<Store> findByOwnerIdAndIdIn(@Nullable @Param("ownerId") UUID ownerId, @Param("ids") Collection<Long> ids);
}
