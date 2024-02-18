package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
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
 * Customer can COUNT every card, but only QUERY Card that is NOT DELETED.<br>
 * Every card.customerId must match current user.
 */
public interface CustomerCardRepository extends CardRepository {
    @PreAuthorize("#customerId == null ? true : authentication.name == #customerId.toString()")
    @Query("""
            select c from Card c
            left join fetch Blueprint b1 on c.blueprint.id=b1.id
            left join fetch Store s on b1.store.id=s.id
            left join fetch Blueprint b2 on b2.store.id=s.id
            where c.customerId = :customerId
            and c.isDeleted = false""")
    Set<Card> findByCustomerId(@Param("customerId") @NonNull UUID customerId);

    @PreAuthorize("#customerId == null ? true : authentication.name == #customerId.toString()")
    @PostFilter("#customerId != null ? true : authentication.name == filterObject.customerId.toString()")
    @Query("""
            select c from Card c
            left join fetch Blueprint b1 on c.blueprint.id=b1.id
            left join fetch Store s on b1.store.id=s.id
            left join fetch Blueprint b2 on b2.store.id=s.id
            where (:customerId is null or c.customerId = :customerId)
            and (c.id in :ids)
            and c.isDeleted = false""")
    Set<Card> findByCustomerIdAndIdIn(@Param("customerId") @Nullable UUID customerId, @Param("ids") @NonNull Collection<Long> ids);

    /**
     * Regardless of card's state, counts every card of target blueprint.
     */
    @Query("select count(c) from Card c where c.blueprint.id = :blueprintId")
    long countByBlueprint_Id(@Param("blueprintId") @NonNull Long blueprintId);

    /**
     * Regardless of card's state, counts customer's every card of target blueprint.
     */
    @PreAuthorize("authentication.name == #customerId.toString()")
    @Query("select count(c) from Card c where c.customerId = :customerId and c.blueprint.id = :id")
    long countByCustomerIdAndBlueprint_Id(@Param("customerId") @NonNull UUID customerId, @Param("id") @NonNull Long id);

    @Override
    @PostAuthorize("returnObject.isEmpty() ? true : authentication.name == returnObject.get().customerId.toString()")
    @Query("select c from Card c where c.id = :id and c.isDeleted = false")
    Optional<Card> findById(@Param("id") Long id);
}
