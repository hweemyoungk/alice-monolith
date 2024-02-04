package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}