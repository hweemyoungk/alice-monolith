package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.NotImplementedException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OwnerStampGrantDtoProcessor implements DtoProcessor<StampGrantDto, Long> {
    private final OwnerCardRepository cardRepository;

    @Override
    public StampGrantDto preprocessForPost(StampGrantDto dto) {
        // id, version, isDeleted
        dto.preprocessBaseForNew();

        final Set<String> violationMessages = new HashSet<>();

        // Authenticate + cardId must exist
        final Card card = cardRepository.findById(dto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, dto.getCardId()));

        // numStamps
        final int maxNumGrant = card.getBlueprint().getNumMaxStamps() - card.getNumCollectedStamps();
        if (dto.getNumStamps() <= maxNumGrant) {
            violationMessages.add(
                    "Granting too many stamps: max allowed is " + maxNumGrant
                            + " but granting " + dto.getNumStamps());
        }

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess StampGrantDto", violationMessages);
        }

        return dto;
    }

    @Override
    public StampGrantDto preprocessForPut(Long id, StampGrantDto dto) {
        throw new NotImplementedException();
    }
}
