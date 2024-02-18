package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface OwnerCardRepository extends CardRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    @Query("""
            select c from Card c
            where c.id = :id
            and c.isDeleted = false""")
    Optional<Card> findById(@Param("id") Long id);
}
