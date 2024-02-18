package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.repositories.RedeemRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;
import java.util.UUID;

/**
 * Customer can only query Redeem that is NOT DELETED and where Redeem.card.customerId matches current user.
 */
public interface CustomerRedeemRepository extends RedeemRepository {
    @PreAuthorize("authentication.name == #customerId.toString()")
    @Query("""
            select (count(r) > 0) from Redeem r
            where r.redeemRequestId = :redeemRequestId
            and r.card.customerId = :customerId
            and r.isDeleted = false""")
    boolean existsByRedeemRequestIdAndCard_CustomerId(@Param("redeemRequestId") @NonNull String redeemRequestId, @Param("customerId") @NonNull UUID customerId);

    /**
     * Currently, used nowhere.
     */
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    @Query("""
            select r from Redeem r
            where r.id = :id
            and r.isDeleted = false""")
    Optional<Redeem> findById(@Param("id") Long id);

    /**
     * Currently, used nowhere.
     */
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    @Query("""
            select r from Redeem r
            where r.redeemRequestId = :redeemRequestId
            and r.isDeleted = false""")
    Optional<Redeem> findByRedeemRequestId(@Param("redeemRequestId") String redeemRequestId);
}
