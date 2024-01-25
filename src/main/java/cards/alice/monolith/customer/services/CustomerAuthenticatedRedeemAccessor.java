package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.repositories.RedeemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerAuthenticatedRedeemAccessor implements AuthenticatedEntityAccessor<Redeem, Long> {
    private final RedeemRepository redeemRepository;

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    public Optional<Redeem> findById(Long id) {
        return redeemRepository.findById(id);
    }

    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().card.customerId.toString()")
    public Optional<Redeem> findByToken(UUID token) {
        return redeemRepository.findByToken(token);
    }
}
