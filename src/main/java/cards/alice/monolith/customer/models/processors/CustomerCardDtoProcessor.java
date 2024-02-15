package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.processors.CardDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.repositories.CustomerBlueprintRepository;
import cards.alice.monolith.customer.repositories.CustomerCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class CustomerCardDtoProcessor implements CardDtoProcessor {
    private final CustomerCardRepository customerCardRepository;
    private final CustomerBlueprintRepository customerBlueprintRepository;

    @Override
    public CardDto preprocessForPost(@Valid CardDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // @NotNull @Positive blueprintId;
        // Validated by @Valid
        // Blueprint must exist
        final Blueprint blueprint = customerBlueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));
        // CustomerBlueprintRepository verifies
        // : Blueprint must be publishing

        // id
        // Overwrite to default
        // version
        // Ignored in entity
        // @NotNull isDeleted
        // Validated by @Valid
        // Overwrite to default
        dto.preprocessBaseForNew();

        // @NotBlank @Length(max = 30) displayName
        // Validated by @Valid

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotNull isDiscarded;
        // Validated by @Valid
        // Overwrite to default
        dto.setIsDiscarded(Boolean.FALSE);

        // @NotNull isUsedOut;
        // Validated by @Valid
        // Overwrite to default
        dto.setIsUsedOut(Boolean.FALSE);

        // @NotNull isInactive;
        // Validated by @Valid
        // Overwrite to default
        dto.setIsInactive(Boolean.FALSE);

        // @NotNull @PositiveOrZero numCollectedStamps;
        // Validated by @Valid
        // Overwrite to default
        dto.setNumCollectedStamps(0);

        // @NotNull @Positive numGoalStamps;
        // Validated by @Valid
        // Overwrite
        // : Goal stamps cannot exceed max stamps of blueprint
        dto.setNumGoalStamps(Math
                .min(dto.getNumGoalStamps(), blueprint.getNumMaxStamps()));

        // expirationDate;
        // Ignored in mapper

        // @NotNull isFavorite;
        // Validated by @Valid

        // @NotNull @PositiveOrZero numRedeemed;
        // Validated by @Valid
        // Overwrite to default
        dto.setNumRedeemed(0);

        // bgImageId;
        // Ignored in input

        // @NotNull customerId;
        // Validated by @Valid
        // Must be current user's name
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!Objects.equals(username, dto.getCustomerId().toString())) {
            violationMessages.add("Unidentified customer ID provided");
        }

        // blueprintDto;
        // Ignored in input

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess CardDto", violationMessages);
        }

        return dto;
    }

    @Override
    public CardDto preprocessForPut(Long id, @Valid CardDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        final Card originalCard = customerCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, id));
        final Blueprint blueprint = originalCard.getBlueprint();

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
        // Customer cannot soft-delete
        if (dto.getIsDeleted()) {
            violationMessages.add("Customer cannot soft-delete card");
        }

        // @NotNull isDiscarded;
        // Validated by @Valid
        // CANNOT shift from TRUE to FALSE
        if (originalCard.getIsDiscarded() && !dto.getIsDiscarded()) {
            violationMessages.add("Customer cannot re-gain card that is already discarded");
        }

        // @NotNull isUsedOut;
        // Validated by @Valid
        // Overwrite to original
        // (Owner can modify during approval)
        dto.setIsUsedOut(originalCard.getIsUsedOut());

        // @NotNull isInactive;
        // Validated by @Valid
        // Overwrite
        dto.setIsInactive(dto.getIsDiscarded() || dto.getIsUsedOut());

        // @NotNull @PositiveOrZero numCollectedStamps;
        // Validated by @Valid
        // Overwrite to original
        dto.setNumCollectedStamps(originalCard.getNumCollectedStamps());

        // @NotNull @Positive numGoalStamps;
        // Validated by @Valid
        // Can be modified
        // Goal stamps cannot exceed max stamps of blueprint
        if (blueprint.getNumMaxStamps() < dto.getNumGoalStamps()) {
            violationMessages.add("Goal stamps cannot exceed max stamps of blueprint");
        }

        // expirationDate;
        // Ignored in mapper

        // @NotNull isFavorite;
        // Validated by @Valid
        // Can be modified

        // @NotNull @PositiveOrZero numRedeemed;
        // Validated by @Valid
        // Overwrite to original
        // (Owner can modify during approval)
        dto.setNumRedeemed(originalCard.getNumRedeemed());

        // bgImageId;
        // Ignored in input

        // @NotNull customerId;
        // Validated by @Valid
        // CANNOT try modifying
        if (originalCard.getCustomerId().compareTo(dto.getCustomerId()) != 0) {
            violationMessages.add("Customer cannot be modified");
        }

        // blueprintDto;
        // Ignored in input

        // @NotNull @Positive blueprintId;
        // Validated by @Valid
        // CANNOT try modifying
        if (!Objects.equals(blueprint.getId(), dto.getBlueprintId())) {
            violationMessages.add("Blueprint cannot be modified");
        }

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess CardDto", violationMessages);
        }

        return dto;
    }
}
