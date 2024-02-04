package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import cards.alice.monolith.customer.repositories.CustomerBlueprintRepository;
import cards.alice.monolith.customer.repositories.CustomerRedeemRuleRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerRedeemRuleServiceImpl implements CustomerRedeemRuleService {
    private final CustomerBlueprintRepository blueprintRepository;
    private final CustomerRedeemRuleRepository redeemRuleRepository;
    private final RedeemRuleMapper redeemRuleMapper;
    private final EntityManager em;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId) {
        final Set<RedeemRule> redeemRules = redeemRuleRepository.findByBlueprint_Id(blueprintId);
        return redeemRules.stream().map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }
}
