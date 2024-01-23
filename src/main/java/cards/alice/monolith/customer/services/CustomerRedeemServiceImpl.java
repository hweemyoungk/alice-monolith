package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRequestDto;
import cards.alice.monolith.common.repositories.RedeemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: Implement
@Service
@RequiredArgsConstructor
public class CustomerRedeemServiceImpl implements CustomerRedeemService {
    private final RedeemRepository redeemRepository;

    @Override
    public Boolean exists(RedeemRequestDto redeemRequestDto) {
        return redeemRepository.findByRedeemRule_IdAndCard_IdAndToken(
                        redeemRequestDto.getRedeemRuleId(), redeemRequestDto.getCardId(), redeemRequestDto.getToken())
                .isPresent();
    }
}
