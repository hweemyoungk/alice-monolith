package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long> {
    @Query("select b from Blueprint b where b.store.id = :id")
    Set<Blueprint> findByStore_Id(@Param("id") Long id);
    @Query("select b from Blueprint b where (:storeId is null or b.store.id = :storeId) and b.id in :ids")
    Set<Blueprint> findByStore_IdAndIdIn(@Param("storeId") Long storeId, @Param("ids") Collection<Long> ids);
}
