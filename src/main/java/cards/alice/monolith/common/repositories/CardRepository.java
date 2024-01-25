package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("select c from Card c where c.customerId = :customerId")
    Set<Card> findByCustomerId(@Param("customerId") @NonNull UUID customerId);
    @Query("select count(c) from Card c where c.customerId = :customerId and c.blueprint.id = :id")
    long countByCustomerIdAndBlueprint_Id(@Param("customerId") @NonNull UUID customerId, @Param("id") @NonNull Long id);

    @Query("select c from Card c where (:customerId is null or c.customerId = :customerId) and (c.id in :ids)")
    Set<Card> findByCustomerIdAndIdIn(@Param("customerId") @Nullable UUID customerId, @Param("ids") @NonNull Collection<Long> ids);
}