package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestDto;

public interface CustomerRedeemRequestService {
    RedeemRequestDto handlePostRedeemRequest(RedeemRequestDto redeemRequestDto);

    boolean exists(RedeemRequestDto redeemRequestDto);

    void deleteRedeemRequest(RedeemRequestDto redeemRequestDto);
}
