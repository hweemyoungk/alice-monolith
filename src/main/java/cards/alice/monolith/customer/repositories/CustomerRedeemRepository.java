package cards.alice.monolith.customer.repositories;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.repositories.RedeemRepository;
import org.springframework.security.access.prepost.PostAuthorize;

import java.util.Optional;

public interface CustomerRedeemRepository extends RedeemRepository {
    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    Optional<Redeem> findById(Long id);

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    Optional<Redeem> findByRedeemRequestId(String redeemRequestId);
}
