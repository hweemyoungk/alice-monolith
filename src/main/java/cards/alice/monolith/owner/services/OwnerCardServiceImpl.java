package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class OwnerCardServiceImpl implements OwnerCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final OwnerAuthenticatedCardAccessor authenticatedCardAccessor;

    @Override
    public Optional<CardDto> getCardById(Long id) {
        return Optional.ofNullable(cardMapper.toDto(
                authenticatedCardAccessor.findById(id).orElse(null)));
    }

    @Override
    public Optional<CardDto> updateCardById(Long id, CardDto cardDto) {
        // Authenticate
        Optional<Card> card = authenticatedCardAccessor.findById(id);

        final var atomicReference = new AtomicReference<Optional<CardDto>>();
        card.ifPresentOrElse(srcCard -> {
            cardDto.setIdVersionNull();
            cardMapper.partialUpdate(cardDto, srcCard);
            final Card savedCard = cardRepository.save(srcCard);
            final CardDto savedCardDto = cardMapper.toDto(savedCard);
            atomicReference.set(Optional.of(savedCardDto));
        }, () -> {
            atomicReference.set(Optional.empty());
        });
        return Optional.empty();
    }
}
