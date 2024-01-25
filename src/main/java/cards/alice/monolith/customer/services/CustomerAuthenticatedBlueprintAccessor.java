package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerAuthenticatedBlueprintAccessor implements AuthenticatedEntityAccessor<Blueprint, Long> {
    private final BlueprintRepository blueprintRepository;

    @Override
    @PostAuthorize("returnObject.isEmpty() ? true : returnObject.get().isPublishing")
    public Optional<Blueprint> findById(Long id) {
        return blueprintRepository.findById(id);
    }
}
