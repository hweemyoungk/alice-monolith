package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.StampGrantDto;
import cards.alice.monolith.common.repositories.StampGrantRepository;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.common.web.mappers.StampGrantMapper;
import cards.alice.monolith.owner.models.processors.OwnerStampGrantDtoProcessor;
import cards.alice.monolith.owner.repositories.OwnerCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerStampGrantServiceImpl implements OwnerStampGrantService {
    private final StampGrantRepository stampGrantRepository;
    private final OwnerCardRepository cardRepository;
    private final StampGrantMapper stampGrantMapper;
    private final CardMapper cardMapper;
    private final OwnerStampGrantDtoProcessor stampGrantDtoProcessor;
    private final OwnerCardService ownerCardService;

    @Override
    @Transactional
    public StampGrantDto grantStampsToCard(StampGrantDto stampGrantDto) {
        final StampGrantDto preprocessedForPost = stampGrantDtoProcessor
                .preprocessForPostSingle(stampGrantDto);

        // Grant
        final Long cardId = preprocessedForPost.getCardId();
        final CardDto cardDto = cardMapper.toDto(
                cardRepository.findById(cardId)
                        .orElseThrow(() -> new ResourceNotFoundException(Card.class, cardId)));
        cardDto.setNumCollectedStamps(
                cardDto.getNumCollectedStamps() + preprocessedForPost.getNumStamps());
        ownerCardService.updateCardById(cardId, cardDto);

        // Save stampGrant
        return stampGrantMapper.toDto(stampGrantRepository
                .save(stampGrantMapper.toEntity(preprocessedForPost)));
    }
}
