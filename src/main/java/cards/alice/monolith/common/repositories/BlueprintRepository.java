package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Blueprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlueprintRepository extends JpaRepository<Blueprint, Long> {
}
