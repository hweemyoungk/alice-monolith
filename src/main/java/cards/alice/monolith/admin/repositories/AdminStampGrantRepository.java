package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.StampGrantRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

public interface AdminStampGrantRepository extends StampGrantRepository {
    @Transactional
    @Modifying
    @Query("delete from StampGrant s where s.card = :card")
    int deleteByCard(@Nullable Card card);
}
