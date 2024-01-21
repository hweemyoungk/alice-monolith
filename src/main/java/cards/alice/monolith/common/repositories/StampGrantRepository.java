package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.StampGrant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampGrantRepository extends JpaRepository<StampGrant, Long> {
}