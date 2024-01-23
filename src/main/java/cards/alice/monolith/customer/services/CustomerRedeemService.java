package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestDto;

public interface CustomerRedeemService {
    Boolean exists(RedeemRequestDto redeemRequestDto);
}
