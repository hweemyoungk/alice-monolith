package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedCardAccessor implements AuthenticatedEntityAccessor<Card, Long> {
    private final CardRepository cardRepository;

    @Override
    @PostAuthorize("authentication.name == returnObject.blueprint.store.ownerId.toString()")
    public Card authenticatedGetById(Long id) {
        return cardRepository.findById(id).orElse(null);
    }
}
