package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.mappers.CardMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class CustomerCardServiceImpl implements CustomerCardService {
    private final EntityManager entityManager;
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    private void setBlueprintReference(Card card, Long id) {
        card.setBlueprint(entityManager.getReference(Blueprint.class, id));
    }

    private boolean validateEntityToSave(Card card) {
        return true;
    }

    @Override
    public CardDto saveNewCard(CardDto cardDto) {
        final Card card = cardMapper.toEntity(cardDto);
        card.setId(null);
        card.setVersion(null);
        return cardMapper.toDto(
                cardRepository.save(card));
    }

    @Override
    public Optional<CardDto> getCardById(Long id) {
        return Optional.ofNullable(
                cardMapper.toDto(
                        cardRepository.findById(id).orElse(null)));
    }

    @Override
    public Optional<CardDto> updateCardById(Long id, CardDto cardDto) {
        final var atomicReference = new AtomicReference<Optional<CardDto>>();
        cardRepository.findById(id).ifPresentOrElse(
                srcCard -> {
                    final Card destCard = cardMapper.toEntity(cardDto);
                    destCard.setId(srcCard.getId());
                    destCard.setVersion(srcCard.getVersion());
                    setBlueprintReference(destCard, srcCard.getId());
                    final Card savedCard = cardRepository.save(destCard);
                    final CardDto savedCardDto = cardMapper.toDto(savedCard);
                    atomicReference.set(Optional.of(savedCardDto));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                }
        );
        return atomicReference.get();
    }

    @Override
    public Optional<CardDto> patchCardById(Long id, CardDto cardDto) {
        final var atomicReference = new AtomicReference<Optional<CardDto>>();
        cardRepository.findById(id).ifPresentOrElse(
                card -> {
                    final Card patchedCard = cardMapper.partialUpdate(cardDto, card);
                    final Card savedCard = cardRepository.save(patchedCard);
                    final CardDto savedCardDto = cardMapper.toDto(savedCard);
                    atomicReference.set(Optional.of(savedCardDto));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                }
        );
        return atomicReference.get();
    }

    @Override
    public Optional<CardDto> softDeleteCardById(Long id) {
        return patchCardById(id, CardDto.builder().isDeleted(true).build());
    }
}
