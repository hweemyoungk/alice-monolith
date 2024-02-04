package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CustomerCardRepository extends CardRepository {
    //@EntityGraph(attributePaths = {"blueprint.store.blueprints"})
    @Query("select c from Card c left join fetch Blueprint b1 on c.blueprint.id=b1.id left join fetch Store s on b1.store.id=s.id left join fetch Blueprint b2 on b2.store.id=s.id where c.customerId = :customerId")
    //@Query("select c from Card c where c.customerId = :customerId")
    Set<Card> findByCustomerId(@Param("customerId") @NonNull UUID customerId);

    @Query("select c from Card c left join fetch Blueprint b1 on c.blueprint.id=b1.id left join fetch Store s on b1.store.id=s.id left join fetch Blueprint b2 on b2.store.id=s.id where (:customerId is null or c.customerId = :customerId) and (c.id in :ids)")
    Set<Card> findByCustomerIdAndIdIn(@Param("customerId") @Nullable UUID customerId, @Param("ids") @NonNull Collection<Long> ids);

    @Query("select count(c) from Card c where c.blueprint.id = :id")
    long countByBlueprint_Id(@Param("id") @NonNull Long id);

    @Query("select count(c) from Card c where c.customerId = :customerId and c.blueprint.id = :id")
    long countByCustomerIdAndBlueprint_Id(@Param("customerId") @NonNull UUID customerId, @Param("id") @NonNull Long id);

    @Override
    @PostAuthorize("returnObject.isEmpty() ? true : authentication.name == returnObject.get().customerId.toString()")
    Optional<Card> findById(Long aLong);
}
