package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.models.processors.RedeemRuleDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerRedeemRuleDtoProcessor implements RedeemRuleDtoProcessor {
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerBlueprintRepository blueprintRepository;

    @Override
    public RedeemRuleDto preprocessForPost(@Valid RedeemRuleDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // blueprintId
        // Blueprint must exist
        final Blueprint blueprint = blueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));

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
