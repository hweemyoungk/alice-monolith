package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.models.RedeemRequestDto;

import java.util.Optional;

public interface CustomerRedeemService {
    Optional<Redeem> exists(RedeemRequestDto redeemRequestDto);
}
