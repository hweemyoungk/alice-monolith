package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Redeem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface RedeemRepository extends JpaRepository<Redeem, Long> {
    @Query("select r from Redeem r where r.token = :token")
    Optional<Redeem> findByToken(@Param("token") @NonNull UUID token);
    @Query("select r from Redeem r where r.redeemRule.id = :id and r.card.id = :id1 and r.token = :token")
    Optional<Redeem> findByRedeemRule_IdAndCard_IdAndToken(@Param("id") Long id, @Param("id1") Long id1, @Param("token") UUID token);
}