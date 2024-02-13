package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.customer.repositories.CustomerRedeemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// TODO: Implement
@Service
@RequiredArgsConstructor
public class CustomerRedeemServiceImpl implements CustomerRedeemService {
    private final CustomerRedeemRepository redeemRepository;

    @Override
    public Optional<Redeem> existByRedeemRequestId(String redeemRequestId) {
        return redeemRepository.findByRedeemRequestId(redeemRequestId);
    }
}
