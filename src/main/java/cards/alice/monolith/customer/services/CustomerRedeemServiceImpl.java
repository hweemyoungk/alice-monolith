package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.web.mappers.RedeemMapper;
import cards.alice.monolith.customer.repositories.CustomerRedeemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

// TODO: Implement
@Service
@RequiredArgsConstructor
public class CustomerRedeemServiceImpl implements CustomerRedeemService {
    private final CustomerRedeemRepository redeemRepository;
    private final RedeemMapper redeemMapper;

    @Override
    public Optional<RedeemDto> getRedeemByRedeemRequestId(String redeemRequestId) {
        return Optional.ofNullable(redeemMapper.toDto(redeemRepository
                .findByRedeemRequestId(redeemRequestId).orElse(null)));
    }

    @Override
    public Boolean existsByRedeemRequestId(String redeemRequestId) {
        final UUID customerId = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        return redeemRepository.existsByRedeemRequestIdAndCard_CustomerId(
                redeemRequestId, customerId);
    }
}
