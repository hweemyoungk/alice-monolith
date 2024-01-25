package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
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
    private final CustomerAuthenticatedBlueprintAccessor authenticatedBlueprintAccessor;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId) {
        final Blueprint blueprint = authenticatedBlueprintAccessor.findById(blueprintId)
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, blueprintId));
        return blueprint.getRedeemRules().stream().map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }
}
