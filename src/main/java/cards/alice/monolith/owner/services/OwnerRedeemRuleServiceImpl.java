package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import cards.alice.monolith.owner.models.processors.OwnerRedeemRuleDtoProcessor;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerRedeemRuleServiceImpl implements OwnerRedeemRuleService {
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final RedeemRuleMapper redeemRuleMapper;
    private final OwnerBlueprintRepository blueprintRepository;
    private final OwnerRedeemRuleDtoProcessor redeemRuleDtoProcessor;

    @Override
    public Optional<RedeemRuleDto> getRedeemRuleById(Long id) {
        return Optional.ofNullable(redeemRuleMapper.toDto(
                redeemRuleRepository.findById(id).orElse(null)));
    }

    @Override
    public Set<RedeemRuleDto> listRedeemRules(Long blueprintId, Set<Long> ids) {
        final Set<RedeemRule> redeemRules;
        if (ids == null) {
            //final Blueprint blueprint = blueprintRepository.findById(blueprintId)
            //        .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, blueprintId));
            //redeemRules = blueprint.getRedeemRules();
            redeemRules = redeemRuleRepository.findByBlueprint_Id(blueprintId);
        } else {
            redeemRules = redeemRuleRepository.findByBlueprint_IdAndIdIn(blueprintId, ids);
        }
        return redeemRules.stream()
                .map(redeemRuleMapper::toDto).collect(Collectors.toSet());
    }

    /**
     * @param redeemRuleDtos Could be mixture of new(post) and modifying(put).
     */
    @Override
    @Transactional
    public Set<RedeemRuleDto> saveRedeemRules(Set<RedeemRuleDto> redeemRuleDtos) {
        return redeemRuleDtos.stream().map(redeemRuleDto -> {
            if (redeemRuleDto.getId() == null) {
                // New: Post
                return saveNewRedeemRule(redeemRuleDto);
            }
            // Modifying: Put
            return updateRedeemRuleById(redeemRuleDto.getId(), redeemRuleDto)
                    .orElse(null);
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public RedeemRuleDto saveNewRedeemRule(RedeemRuleDto redeemRuleDto) {
        final RedeemRuleDto preprocessedForPost = redeemRuleDtoProcessor
                .preprocessForPost(redeemRuleDto);
        return redeemRuleMapper.toDto(redeemRuleRepository
                .save(redeemRuleMapper.toEntity(preprocessedForPost)));
    }

    @Override
    @Transactional
    public Optional<RedeemRuleDto> updateRedeemRuleById(Long id, RedeemRuleDto redeemRuleDto) {
        final RedeemRuleDto preprocessedForPut = redeemRuleDtoProcessor
                .preprocessForPut(id, redeemRuleDto);
        return Optional.of(redeemRuleMapper.toDto(redeemRuleRepository.save(redeemRuleMapper.toEntity(preprocessedForPut))));
    }

    @Override
    @Transactional
    public Optional<RedeemRuleDto> patchRedeemRuleById(Long id, RedeemRuleDto redeemRuleDto) {
        final var atomicReference = new AtomicReference<Optional<RedeemRuleDto>>();
        redeemRuleRepository.findById(id).ifPresentOrElse(
                srcRedeemRule -> {
                    final RedeemRule redeemRuleToSave = redeemRuleMapper.partialUpdate(redeemRuleDto, srcRedeemRule);
                    final RedeemRuleDto savedRedeemRuleDto = redeemRuleMapper.toDto(
                            redeemRuleRepository.save(redeemRuleToSave));
                    atomicReference.set(Optional.of(savedRedeemRuleDto));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                }
        );
        return atomicReference.get();
    }
}
