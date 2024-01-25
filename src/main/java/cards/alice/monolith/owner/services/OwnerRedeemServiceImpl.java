package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Redeem;
import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.repositories.RedeemRepository;
import cards.alice.monolith.common.web.mappers.RedeemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerRedeemServiceImpl implements OwnerRedeemService {
    private final RedeemRepository redeemRepository;
    private final RedeemMapper redeemMapper;

    @Override
    public RedeemDto saveNewRedeem(RedeemDto redeemDto) {
        redeemDto.setIdVersionNull();
        final Redeem redeem = redeemMapper.toEntity(redeemDto);
        return redeemMapper.toDto(redeemRepository.save(redeem));
    }
}
