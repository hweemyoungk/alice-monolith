package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import cards.alice.monolith.customer.repositories.CustomerBlueprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerRedeemRuleServiceImpl implements CustomerRedeemRuleService {
    private final CustomerBlueprintRepository blueprintRepository;
    private final RedeemRuleMapper redeemRuleMapper;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId) {
        final Blueprint blueprint = blueprintRepository.findById(blueprintId)
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, blueprintId));
        return blueprint.getRedeemRules().stream().map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }
}
