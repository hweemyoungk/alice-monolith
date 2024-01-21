package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("select s from Store s where s.ownerId = :ownerId and s.id in :ids")
    Set<Store> findByOwnerIdAndIdIn(@Param("ownerId") UUID ownerId, @Param("ids") Collection<Long> ids);
    //@Query("select s from Store s left join s.blueprints b left join b.redeemRules r where s.id in :ids")
    //@Query(value = "select * from store as s where s.id in :ids", nativeQuery = true)
    Set<Store> findByIdIn(@Param("ids") Collection<Long> ids);
}
