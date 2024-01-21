package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.CardDto;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CustomerCardService {
    CardDto saveNewCard(CardDto cardDto);

    Optional<CardDto> getCardById(Long id);

    Optional<CardDto> updateCardById(Long id, CardDto cardDto);

    Optional<CardDto> patchCardById(Long id, CardDto cardDto);

    Optional<CardDto> softDeleteCardById(Long id);

    Set<CardDto> listCards(UUID userId, Set<Long> ids);

    Long getNumIssues(UUID customerId, Long blueprintId);
}
