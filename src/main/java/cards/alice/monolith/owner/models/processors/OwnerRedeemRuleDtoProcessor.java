package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.models.processors.RedeemRuleDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerRedeemRuleDtoProcessor extends RedeemRuleDtoProcessor {
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerBlueprintRepository blueprintRepository;
    private final Map<String, OwnerMembershipDto> ownerMembershipMap;

    @Override
    public Collection<RedeemRuleDto> preprocessForPost(@NotEmpty @Valid Collection<RedeemRuleDto> dtos) {
        checkBoundToSingleBlueprint(dtos);
        return super.preprocessForPost(dtos);
    }

    private void checkBoundToSingleBlueprint(Collection<RedeemRuleDto> dtos) {
        final Set<Long> ids = dtos.stream().map(RedeemRuleDto::getBlueprintId).collect(Collectors.toSet());
        if (ids.contains(null) || ids.size() != 1) {
            throw new DtoProcessingException("Failed to preprocess RedeemRuleDtos",
                    Set.of("RedeemRuleDtos bound to multiple blueprints: " + ids));
        }
    }

    @Override
    protected void checkMembershipForPost(Collection<RedeemRuleDto> dtos) {
        final Set<String> violationMessages = new HashSet<>();
        final UUID ownerId = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        final OwnerMembershipDto highestOwnerMembership = (OwnerMembershipDto) MembershipDto
                .highestPriority(OwnerMembershipDto
                        .getCurrentOwnerMemberships(ownerMembershipMap));

        final Optional<RedeemRuleDto> sampleDto = dtos.stream().findAny();
        if (sampleDto.isEmpty()) {
            violationMessages.add("No RedeemRuleDto provided");
            throw new DtoProcessingException("Failed to check membership for RedeemRuleDtos", violationMessages);
        }

        final Long blueprintId = sampleDto.get().getBlueprintId();

        // @Min(-1) numMaxAccumulatedTotalStores;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalStores;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveStores;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalBlueprintsPerStore;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveBlueprintsPerStore;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalRedeemRulesPerBlueprint;
        // NOT includes DELETED redeemRules
        if (highestOwnerMembership.getNumMaxCurrentTotalRedeemRulesPerBlueprint() != -1) {
            long numCurrentTotalPerBlueprint = redeemRuleRepository.exclusiveCountByBlueprint_IdAndBlueprint_Store_OwnerIdAndIsDeleted(
                    blueprintId, ownerId, Boolean.FALSE);
            if (highestOwnerMembership.getNumMaxCurrentTotalRedeemRulesPerBlueprint() < numCurrentTotalPerBlueprint + dtos.size()) {
                violationMessages.add("Exceeded maximum current total redeem rules of blueprint.");
            }
        }

        // @Min(-1) numMaxCurrentActiveRedeemRulesPerBlueprint;
        // Currently, there's no active/inactive redeem rules.

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to check membership for RedeemRuleDtos", violationMessages);
        }
    }

    @Override
    protected RedeemRuleDto preprocessForPost(RedeemRuleDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // blueprintId
        // Blueprint must exist
        final Blueprint blueprint = blueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));
        // Owner cannot create redeem rule of expired blueprint
        if (blueprint.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("Owner cannot create redeem rule of expired blueprint");
        }

        // id
        // Should be null
        // version
        // Ignored in entity
        // @NotNull isDeleted
        // Validated by @Valid
        // Should be false
        dto.preprocessBaseForNew();

        // @NotBlank @Length(max = 30) displayName
        // Validated by @Valid
        // Can be modified

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotBlank @Length(max = 100) description;
        // Validated by @Valid

        // @NotNull @PositiveOrZero consumes;
        // Validated by @Valid
        // Should not exceed blueprint.numMaxStamps
        if (blueprint.getNumMaxStamps() < dto.getConsumes()) {
            violationMessages.add("Cannot consume " + dto.getConsumes() +
                    " which is over max stamps (" + blueprint.getNumMaxStamps() +
                    ") of blueprint");
        }

        // imageId
        // TODO: Query storage to check if file exists

        // blueprintDto
        // Ignored in input (preprocess)

        // redeemDtos
        // Ignored in input (preprocess)

        // TODO: Isn't there concurrency issue when we implement and use bulk-check membership?

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess RedeemRuleDto", violationMessages);
        }

        return dto;
    }

    @Override
    public RedeemRuleDto preprocessForPut(Long id, @Valid RedeemRuleDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        final RedeemRule originalRedeemRule = redeemRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, id));
        final Blueprint blueprint = originalRedeemRule.getBlueprint();

        // id
        // Should not be modified
        dto.setId(id);

        // version
        // Ignored in entity

        // @NotBlank @Length(max = 30) displayName
        // Validated by @Valid
        // Can be modified

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotNull isDeleted
        // Validated by @Valid
        // Owner cannot soft-delete
        if (dto.getIsDeleted()) {
            violationMessages.add("Owner cannot soft-delete redeem rule");
        }

        // @NotBlank @Length(max = 100) description;
        // Validated by @Valid
        // Can be modified

        // @NotNull @PositiveOrZero consumes;
        // Validated by @Valid
        // Can be modified
        // TODO: Is it OK to modify(increase, especially) consumes?
        // Should not exceed blueprint.numMaxStamps
        if (blueprint.getNumMaxStamps() < dto.getConsumes()) {
            violationMessages.add("Cannot consume " + dto.getConsumes() +
                    " which is over max stamps (" + blueprint.getNumMaxStamps() +
                    ") of blueprint");
        }

        // imageId
        // Can be modified
        // TODO: Query storage to check if file exists

        // blueprintDto
        // Ignored in input (preprocess)

        // blueprintId
        // Should not be modified
        if (!Objects.equals(blueprint.getId(), dto.getBlueprintId())) {
            violationMessages.add("Blueprint cannot be changed");
        }

        // redeemDtos
        // Ignored in input (preprocess)

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess RedeemRuleDto", violationMessages);
        }

        return dto;
    }
}
