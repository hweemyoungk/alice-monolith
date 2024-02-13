package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.RedeemRequestNewDto;

import java.util.Set;
import java.util.UUID;

public interface OwnerRedeemRequestService {
    Set<RedeemRequestNewDto> listRedeemRequests(UUID ownerId);
    void approveRedeemRequest(String id);
    void deleteRedeemRequestById(String id);
}
