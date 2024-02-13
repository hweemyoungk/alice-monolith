package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Redeem;

import java.util.Optional;

public interface CustomerRedeemService {
    Optional<Redeem> existByRedeemRequestId(String redeemRequestDto);
}
