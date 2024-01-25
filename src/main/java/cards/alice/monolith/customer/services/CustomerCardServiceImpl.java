package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.repositories.CardRepository;
import cards.alice.monolith.common.web.mappers.CardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerCardServiceImpl implements CustomerCardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final CustomerAuthenticatedCardAccessor authenticatedCardAccessor;

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
        return Optional.ofNullable(cardMapper.toDto(
                authenticatedCardAccessor.findById(id).orElse(null)));
    }

    // Tested
    @Override
    public Optional<CardDto> updateCardById(Long id, CardDto cardDto) {
        Optional<Card> card = authenticatedCardAccessor.findById(id);
        final var atomicReference = new AtomicReference<Optional<CardDto>>();
        card.ifPresentOrElse(
                srcCard -> {
                    cardDto.setId(null);
                    cardDto.setVersion(null);
                    cardMapper.partialUpdate(cardDto, srcCard);
                    final Card savedCard = cardRepository.save(srcCard);
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
        Optional<Card> card = authenticatedCardAccessor.findById(id);
        final var atomicReference = new AtomicReference<Optional<CardDto>>();
        card.ifPresentOrElse(
                srcCard -> {
                    final Card patchedCard = cardMapper.partialUpdate(cardDto, srcCard);
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
        CardDto cardDto = CardDto.builder()
                .isDiscarded(true)
                .isDeleted(true)
                .isInactive(true)
                .build();
        return patchCardById(id, cardDto);
    }

    @Override
    public Set<CardDto> listCards(UUID customerId, Set<Long> ids) {
        final Set<Card> cards;
        if (ids == null) {
            cards = cardRepository.findByCustomerId(customerId);
        } else {
            cards = cardRepository.findByCustomerIdAndIdIn(customerId, ids);
        }
        return cards.stream()
                .map(cardMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public Long getNumIssues(UUID customerId, Long blueprintId) {
        return cardRepository.countByCustomerIdAndBlueprint_Id(customerId, blueprintId);
    }
}
