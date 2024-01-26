package cards.alice.monolith.owner.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface OwnerCardRepository extends CardRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    Optional<Card> findById(Long aLong);
}
