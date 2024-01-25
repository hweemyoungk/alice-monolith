package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Card;
import cards.alice.monolith.common.repositories.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerAuthenticatedCardAccessor implements AuthenticatedEntityAccessor<Card, Long> {
    private final CardRepository cardRepository;

    @Override
    @PostAuthorize("returnObject.empty ? true : authentication.name == returnObject.get().blueprint.store.ownerId.toString()")
    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }
}
