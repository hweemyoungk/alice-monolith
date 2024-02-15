package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemDto;

import java.util.Optional;

public interface CustomerRedeemService {
    Optional<RedeemDto> getRedeemByRedeemRequestId(String redeemRequestId);

    Boolean existsByRedeemRequestId(String redeemRequestId);
}
