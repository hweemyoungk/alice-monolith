package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.repositories.RedeemRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRedeemRepository extends RedeemRepository {
    @Query("""
            select (count(r) > 0) from Redeem r
            where r.redeemRequestId = :redeemRequestId and r.card.customerId = :customerId""")
    boolean existsByRedeemRequestIdAndCard_CustomerId(@Param("redeemRequestId") @NonNull String redeemRequestId, @Param("customerId") @NonNull UUID customerId);

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    Optional<Redeem> findById(Long id);

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    Optional<Redeem> findByRedeemRequestId(String redeemRequestId);
}
