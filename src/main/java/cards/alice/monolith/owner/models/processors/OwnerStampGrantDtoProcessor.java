package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.models.processors.StampGrantDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerStampGrantDtoProcessor extends StampGrantDtoProcessor {
    private final OwnerCardRepository cardRepository;

    @Override
    protected void checkMembershipForPost(Collection<StampGrantDto> dtos) {
        // NO-OP: Currently, StampGrant is not constrained by membership.
    }

    @Override
    protected StampGrantDto preprocessForPost(StampGrantDto dto) {
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
        // Shared lock: must not be modified
        final Card card = cardRepository.sharedFindById(dto.getCardId())
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

        // @PositiveOrZero numStampsBefore;
        // Overwrite
        dto.setNumStampsBefore(card.getNumCollectedStamps());

        // @Positive numStampsAfter;
        // Overwrite
        dto.setNumStampsAfter(card.getNumCollectedStamps() + dto.getNumStamps());

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
