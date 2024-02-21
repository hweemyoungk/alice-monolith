package cards.alice.monolith.admin.repositories;

import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AdminRedeemRuleRepository extends RedeemRuleRepository {
    @Transactional
    @Modifying
    @Query("""
            update RedeemRule r set r.isDeleted = :isDeleted
            where r.blueprint.store.id in (
            select s.id from Store s where s.ownerId = :ownerId)""")
    int updateIsDeletedByBlueprint_Store_OwnerId(@NonNull @Param("isDeleted") Boolean isDeleted, @NonNull @Param("ownerId") UUID ownerId);

}
