package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerStampGrantDtoProcessor implements DtoProcessor<StampGrantDto, Long> {
    private final OwnerCardRepository cardRepository;

    @Override
    public StampGrantDto preprocessForPost(@Valid StampGrantDto dto) {
        final Set<String> violationMessages = new HashSet<>();

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

        // @NotNull @Positive cardId;
        // Card must exist
        // OwnerCardRepository authenticates
        // : Owner should own card.blueprint.store
        final Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, dto.getCardId()));
        // Card must be active
        if (card.getIsInactive()) {
            violationMessages.add("Card must be active");
        }

        // @NotNull @Positive numStamps;
        // Validated by @Valid
        // card.numCollectedStamps should not exceed blueprint.numMaxStamp after grant
        final int maxNumGrant = card.getBlueprint().getNumMaxStamps() - card.getNumCollectedStamps();
        if (maxNumGrant < dto.getNumStamps()) {
            violationMessages.add(
                    "Granting too many stamps: max allowed is " + maxNumGrant
                            + " but granting " + dto.getNumStamps());
        }

        // cardDto;
        // Ignored in input

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess StampGrantDto", violationMessages);
        }

        return dto;
    }

    @Override
    public StampGrantDto preprocessForPut(Long id, @Valid StampGrantDto dto) {
        throw new UnsupportedOperationException();
    }
}
