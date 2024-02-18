package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Customer can only query store that is NOT DELETED and NOT INACTIVE.
 */
public interface CustomerStoreRepository extends StoreRepository {
    @Override
    @Query("""
            select s from Store s
            where s.id = :id
            and s.isInactive = false
            and s.isDeleted = false""")
    Optional<Store> findById(@Param("id") Long id);

    /**
     * Currently, used nowhere.
     */
    @Override
    @Query("""
            select s from Store s
            left join fetch Blueprint b on b.store.id = s.id
            left join fetch RedeemRule rr on rr.blueprint.id = b.id
            where s.ownerId = :ownerId
            and s.isInactive = false
            and s.isDeleted = false""")
    Set<Store> findByOwnerId(@Param("ownerId") @NonNull UUID ownerId);

    /**
     * Currently, used nowhere.
     */
    @Override
    @Query("""
            select s from Store s
            where (:ownerId is null or s.ownerId = :ownerId)
            and s.id in :ids
            and s.isInactive = false
            and s.isDeleted = false""")
    Set<Store> findByOwnerIdAndIdIn(@Param("ownerId") @Nullable UUID ownerId, @Param("ids") @NonNull Collection<Long> ids);

}
