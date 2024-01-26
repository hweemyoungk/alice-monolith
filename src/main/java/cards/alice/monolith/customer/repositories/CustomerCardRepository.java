package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface CustomerCardRepository extends CardRepository {
    @Override
    @PostAuthorize("returnObject.isEmpty() ? true : authentication.name == returnObject.get().customerId.toString()")
    Optional<Card> findById(Long aLong);
}
