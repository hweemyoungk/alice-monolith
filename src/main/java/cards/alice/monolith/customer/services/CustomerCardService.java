package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.CardDto;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CustomerCardService {
    CardDto saveNewCard(CardDto cardDto);

    Optional<CardDto> getCardById(Long id);

    Optional<CardDto> updateCardById(Long id, CardDto cardDto);

    Optional<CardDto> patchCardById(Long id, CardDto cardDto);

    Optional<CardDto> softDeleteCardById(Long id);

    @PostFilter("authentication.name == filterObject.customerId.toString()")
    Set<CardDto> listCards(UUID customerId, Set<Long> ids);

    Long getNumIssues(UUID customerId, Long blueprintId);
}
