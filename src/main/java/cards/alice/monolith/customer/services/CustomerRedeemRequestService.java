package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestDto;

public interface CustomerRedeemRequestService {
    RedeemRequestDto saveNewRedeemRequest(RedeemRequestDto redeemRequestDto);

    Boolean exists(String id);

    void deleteRedeemRequestById(String id);
}
