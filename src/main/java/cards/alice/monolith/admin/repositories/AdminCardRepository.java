package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.repositories.CardRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AdminCardRepository extends CardRepository {
    @Transactional
    @Modifying
    @Query("update Card c set c.isDeleted = :isDeleted where c.customerId = :customerId")
    int updateIsDeletedByCustomerId(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("customerId") UUID customerId);
}
