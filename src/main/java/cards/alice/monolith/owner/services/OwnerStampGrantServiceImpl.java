package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.domain.StampGrant;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.repositories.StampGrantRepository;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.common.web.mappers.StampGrantMapper;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerStampGrantServiceImpl implements OwnerStampGrantService {
    private final OwnerCardRepository cardRepository;
    private final StampGrantRepository stampGrantRepository;
    private final StampGrantMapper stampGrantMapper;
    private final CardMapper cardMapper;
    private final OwnerCardService ownerCardService;

    @Override
    public StampGrantDto saveNewStampGrant(StampGrantDto stampGrantDto) {
        final StampGrant stampGrant = stampGrantMapper.toEntity(stampGrantDto);
        stampGrant.setId(null);
        stampGrant.setVersion(null);
        return stampGrantMapper.toDto(
                stampGrantRepository.save(stampGrant));
    }

    @Override
    @Transactional
    public StampGrantDto grantStampsToCard(StampGrantDto stampGrantDto) {
        // Validate(Includes authenticate)
        validateGrant(stampGrantDto);

        // Grant
        final Card card = cardRepository.findById(stampGrantDto.getCardId()).get();
        CardDto cardDto = cardMapper.toDto(card);
        cardDto.setNumCollectedStamps(
                cardDto.getNumCollectedStamps() + stampGrantDto.getNumStamps());
        ownerCardService.updateCardById(cardDto.getId(), cardDto);

        // Save stampGrant
        return saveNewStampGrant(stampGrantDto);
    }

    private void validateGrant(StampGrantDto stampGrantDto) {
        // Authenticate
        final Card card = cardRepository.findById(stampGrantDto.getCardId())
                .orElseThrow(() -> new ResourceNotFoundException(Card.class, stampGrantDto.getCardId()));

        final int maxNumGrant = card.getBlueprint().getNumMaxStamps() - card.getNumCollectedStamps();
        if (stampGrantDto.getNumStamps() <= maxNumGrant) {
            throw new IllegalArgumentException("Granting too many stamps: max allowed is " + maxNumGrant + " but granting " + stampGrantDto.getNumStamps());
        }
    }
}
