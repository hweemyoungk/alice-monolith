package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.StagedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StagedUserRepository extends JpaRepository<StagedUser, Long> {
}