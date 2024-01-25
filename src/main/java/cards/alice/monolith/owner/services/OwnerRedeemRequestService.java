package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemRequestDto;

import java.util.Set;
import java.util.UUID;

public interface OwnerRedeemRequestService {
    Set<RedeemRequestDto> listRedeemRequests(UUID ownerId);
    void approveRedeemRequest(RedeemRequestDto redeemRequestDto);
    void deleteRedeemRequest(RedeemRequestDto redeemRequestDto);
}
