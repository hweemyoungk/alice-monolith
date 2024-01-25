package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.models.RedeemRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// TODO: Implement
@Service
@RequiredArgsConstructor
public class CustomerRedeemServiceImpl implements CustomerRedeemService {
    private final CustomerAuthenticatedRedeemAccessor authenticatedRedeemAccessor;

    @Override
    public Optional<Redeem> exists(RedeemRequestDto redeemRequestDto) {
        return authenticatedRedeemAccessor.findByToken(redeemRequestDto.getToken());
    }
}
