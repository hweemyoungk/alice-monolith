package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CustomerRedeemService {
    @PreAuthorize("authentication.name == redeemRequestDto.customerId.toString()")
    Boolean exists(RedeemRequestDto redeemRequestDto);
}
