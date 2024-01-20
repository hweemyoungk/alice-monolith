package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.CardDto;

import java.util.Optional;

public interface CustomerCardService {
    CardDto saveNewCard(CardDto cardDto);
    Optional<CardDto> getCardById(Long id);
    Optional<CardDto> updateCardById(Long id, CardDto cardDto);
    Optional<CardDto> patchCardById(Long id, CardDto cardDto);
    Optional<CardDto> softDeleteCardById(Long id);
}
