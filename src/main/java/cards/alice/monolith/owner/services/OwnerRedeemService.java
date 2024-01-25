package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemDto;

public interface OwnerRedeemService {
    RedeemDto saveNewRedeem(RedeemDto redeemDto);
}
