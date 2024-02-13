package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestNewDto;

public interface CustomerRedeemRequestService {
    RedeemRequestNewDto handlePostRedeemRequest(RedeemRequestNewDto redeemRequestDtoFromCustomer);

    boolean exists(String id);

    void deleteRedeemRequest(String id);
}
