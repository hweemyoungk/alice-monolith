package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.web.mappers.CardMapper;
import cards.alice.monolith.customer.models.processors.CustomerCardDtoProcessor;
import cards.alice.monolith.customer.repositories.CustomerCardRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerCardServiceImpl implements CustomerCardService {
    private final CustomerCardRepository cardRepository;
    private final CustomerCardDtoProcessor cardProcessor;
    private final CardMapper cardMapper;
    private final EntityManager em;

    @Override
    @Transactional
    public CardDto saveNewCard(CardDto cardDto) {
        final CardDto preprocessedForPost = cardProcessor.preprocessForPost(cardDto);
        final Card card = cardMapper.toEntity(preprocessedForPost);
        return cardMapper.toDto(
                cardRepository.save(card));
    }

    @Override
    public Optional<CardDto> getCardById(Long id) {
        return Optional.ofNullable(cardMapper.toDto(
                cardRepository.findById(id).orElse(null)));
    }

    // Tested
    @Override
    @Transactional
    public Optional<CardDto> updateCardById(Long id, CardDto cardDto) {
        final CardDto preprocessedForPut = cardProcessor.preprocessForPut(id, cardDto);
        return Optional.of(cardMapper.toDto(cardRepository
                .save(cardMapper.toEntity(preprocessedForPut))));
    }

    @Override
    @Transactional
    public Optional<CardDto> patchCardById(Long id, CardDto cardDto) {
        final Optional<Card> card = cardRepository.findById(id);
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
    @Transactional
    public Optional<CardDto> discardCardById(Long id) {
        CardDto cardDto = CardDto.builder()
                .isDiscarded(true)
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

        return cards.stream().map(cardMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public Long getNumIssues(UUID customerId, Long blueprintId) {
        return cardRepository.countByCustomerIdAndBlueprint_Id(customerId, blueprintId);
    }
}
