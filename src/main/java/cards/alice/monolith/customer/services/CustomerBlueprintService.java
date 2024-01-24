package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.BlueprintDto;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.Optional;
import java.util.Set;

public interface CustomerBlueprintService {
    @PostAuthorize("returnObject.isPublishing")
    BlueprintDto authorizedGetBlueprintById(Long id);
    Optional<BlueprintDto> getBlueprintById(Long id);
    @PostFilter("filterObject.isPublishing")
    Set<BlueprintDto> listBlueprints(Long storeId, Set<Long> ids);
}
