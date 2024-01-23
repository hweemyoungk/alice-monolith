package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.config.AuthenticatedEntityAccessor;
import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedBlueprintAccessor implements AuthenticatedEntityAccessor<Blueprint, Long> {
    private final BlueprintRepository blueprintRepository;

    @Override
    @PostAuthorize("returnObject.isPublishing")
    public Blueprint authenticatedGetById(Long id) {
        return blueprintRepository.findById(id).orElse(null);
    }
}
