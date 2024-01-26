package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerRedeemRuleServiceImpl implements OwnerRedeemRuleService {
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final RedeemRuleMapper redeemRuleMapper;
    private final OwnerBlueprintRepository blueprintRepository;

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId, Set<Long> ids) {
        final Set<RedeemRule> redeemRules;
        if (ids == null) {
            final Blueprint blueprint = blueprintRepository.findById(blueprintId)
                    .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, blueprintId));
            redeemRules = blueprint.getRedeemRules();
        } else {
            redeemRules = redeemRuleRepository.findByBlueprint_IdAndIdIn(blueprintId, ids);
        }
        return redeemRules.stream()
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
