package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
