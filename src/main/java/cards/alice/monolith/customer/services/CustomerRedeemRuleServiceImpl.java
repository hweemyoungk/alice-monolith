package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerRedeemRuleServiceImpl implements CustomerRedeemRuleService {
    private final RedeemRuleRepository redeemRuleRepository;
    private final RedeemRuleMapper redeemRuleMapper;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId) {
        return redeemRuleRepository.findByBlueprint_Id(blueprintId).stream()
                .map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }
}
