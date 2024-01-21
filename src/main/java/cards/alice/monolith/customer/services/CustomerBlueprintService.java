package cards.alice.monolith.customer.services;

import cards.alice.monolith.common.models.BlueprintDto;

import java.util.Optional;
import java.util.Set;

public interface CustomerBlueprintService {
    Optional<BlueprintDto> getBlueprintById(Long id);

    Set<BlueprintDto> listBlueprints(Long storeId, Set<Long> ids);
}
