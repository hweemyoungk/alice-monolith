package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.CardDto;

import java.util.Optional;

public interface OwnerCardService {
    Optional<CardDto> getCardById(Long id);
    Optional<CardDto> updateCardById(Long id, CardDto cardDto);
}
