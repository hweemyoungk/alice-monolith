package cards.alice.monolith.common.repositories;

import cards.alice.monolith.common.domain.Redeem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface RedeemRepository extends JpaRepository<Redeem, Long> {
    @Query("select r from Redeem r where r.redeemRequestId = :redeemRequestId")
    Optional<Redeem> findByRedeemRequestId(@Param("redeemRequestId") @NonNull String redeemRequestId);
}