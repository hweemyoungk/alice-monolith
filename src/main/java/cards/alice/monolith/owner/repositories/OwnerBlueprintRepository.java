package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Owner can only query Blueprint that is NOT DELETED and where Blueprint.store.ownerId matches current user.
 */
public interface OwnerBlueprintRepository extends BlueprintRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().store.ownerId.toString()")
    @Query("""
            select b from Blueprint b
            where b.id = :id
            and b.isDeleted = false""")
    Optional<Blueprint> findById(@Param("id") Long id);

    @Override
    @PostFilter("authentication.name == filterObject.store.ownerId.toString()")
    @Query("""
            select b from Blueprint b
            where b.store.id = :id
            and b.isDeleted = false""")
    Set<Blueprint> findByStore_Id(Long id);

    @Override
    @PostFilter("authentication.name == filterObject.store.ownerId.toString()")
    @Query("""
            select b from Blueprint b
            where (:storeId is null or b.store.id = :storeId)
            and b.id in :ids
            and b.isDeleted = false""")
    Set<Blueprint> findByStore_IdAndIdIn(@Nullable @Param("storeId") Long storeId, @NonNull @Param("ids") Collection<Long> ids);

}
