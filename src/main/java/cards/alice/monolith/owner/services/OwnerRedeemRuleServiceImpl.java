package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerRedeemRuleServiceImpl implements OwnerRedeemRuleService {
    private final RedeemRuleRepository redeemRuleRepository;
    private final RedeemRuleMapper redeemRuleMapper;
    private final AuthenticatedBlueprintAccessor authenticatedBlueprintAccessor;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId) {
        // Authenticate
        final Blueprint blueprint = authenticatedBlueprintAccessor.authenticatedGetById(blueprintId);
        return blueprint.getRedeemRules().stream()
                .map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }

    /**
     * @param redeemRuleDtos Could be mixture of old and brand new.
     */
    @Override
    public Set<RedeemRuleDto> saveRedeemRules(Set<RedeemRuleDto> redeemRuleDtos) {
        return redeemRuleRepository.saveAll(redeemRuleDtos.stream()
                        .map(redeemRuleMapper::toEntity).collect(Collectors.toSet())).stream()
                .map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }
}
