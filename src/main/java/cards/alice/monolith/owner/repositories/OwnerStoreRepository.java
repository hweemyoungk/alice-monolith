package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.repositories.StoreRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Owner can only query store that is NOT DELETED<br>
 * and where store.ownerId matches current user.
 */
public interface OwnerStoreRepository extends StoreRepository {
    @PreAuthorize("authentication.name == #ownerId.toString()")
    @Query("select count(s) from Store s where s.ownerId = :ownerId")
    long countByOwnerId(@Param("ownerId") @NonNull UUID ownerId);

    @PreAuthorize("authentication.name == #ownerId.toString()")
    @Query("""
            select count(s) from Store s
            where s.isDeleted = :isDeleted
            and s.ownerId = :ownerId""")
    long countByIsDeletedAndOwnerId(@Param("isDeleted") @NonNull Boolean isDeleted, @Param("ownerId") @NonNull UUID ownerId);

    @PreAuthorize("authentication.name == #ownerId.toString()")
    @Query("""
            select count(s) from Store s
            where s.isDeleted = :isDeleted
            and s.isInactive = :isInactive
            and s.ownerId = :ownerId""")
    long countByIsDeletedAndIsInactiveAndOwnerId(@Param("isDeleted") @NonNull Boolean isDeleted, @Param("isInactive") @NonNull Boolean isInactive, @Param("ownerId") @NonNull UUID ownerId);

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    @Query("""
            select s from Store s
            where s.id = :id
            and s.isDeleted = false""")
    Optional<Store> findById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().ownerId.toString()")
    @Query("""
            select s from Store s
            where s.id = :id
            and s.isDeleted = false""")
    Optional<Store> exclusiveFindById(@Param("id") Long id);

    @Override
    @PreAuthorize("authentication.name == #ownerId.toString()")
    @Query("""
            select s from Store s
            left join fetch Blueprint b on b.store.id = s.id
            left join fetch RedeemRule rr on rr.blueprint.id = b.id
            where s.ownerId = :ownerId
            and s.isDeleted = false""")
    Set<Store> findByOwnerId(@Param("ownerId") @NonNull UUID ownerId);

    @Override
    @PreAuthorize("#ownerId == null ? true : authentication.name == #ownerId.toString()")
    @PostFilter("#ownerId != null ? true : authentication.name == filterObject.ownerId.toString()")
    @Query("""
            select s from Store s
            where (:ownerId is null or s.ownerId = :ownerId)
            and s.id in :ids
            and s.isDeleted = false""")
    Set<Store> findByOwnerIdAndIdIn(@Param("ownerId") @Nullable UUID ownerId, @Param("ids") @NonNull Collection<Long> ids);
}
