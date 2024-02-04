package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.models.processors.RedeemRuleDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerRedeemRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OwnerRedeemRuleDtoProcessor implements RedeemRuleDtoProcessor {
    private final OwnerRedeemRuleRepository redeemRuleRepository;
    private final OwnerBlueprintRepository blueprintRepository;
    private final SmartValidator validator;

    @Override
    public RedeemRuleDto preprocessForPost(RedeemRuleDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id, version, isDeleted
        dto.preprocessBaseForNew();

        // description, consumes
        final Errors validationErrors = validator.validateObject(dto);
        validationErrors.getFieldErrors().forEach(error -> violationMessages.add(error.toString()));
        validationErrors.getGlobalErrors().forEach(error -> violationMessages.add(error.toString()));

        // imageId
        // Can be null
        // TODO: Query storage to check if file exists

        // blueprintId must exist
        blueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));

        // blueprintDto
        // Ignore

        // redeemDtos
        // Can be null

        return dto;
    }

    @Override
    public RedeemRuleDto preprocessForPut(Long id, RedeemRuleDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id, version
        dto.setIdVersionNull();

        // isDeleted, description, consumes
        final Errors validationErrors = validator.validateObject(dto);
        validationErrors.getFieldErrors().forEach(error -> violationMessages.add(error.toString()));
        validationErrors.getGlobalErrors().forEach(error -> violationMessages.add(error.toString()));

        // imageId
        // Can be null
        // TODO: Query storage to check if file exists

        // blueprintId cannot be modified
        final RedeemRule originalRedeemRule = redeemRuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RedeemRule.class, id));
        if (!Objects.equals(originalRedeemRule.getBlueprint().getId(), dto.getBlueprintId())) {
            violationMessages.add("Blueprint cannot be changed");
        }

        // blueprintDto
        // Ignore

        // redeemDtos
        // Can be null

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess RedeemRuleDto", violationMessages);
        }

        return dto;
    }
}
