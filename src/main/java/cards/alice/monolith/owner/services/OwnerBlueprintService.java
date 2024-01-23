package cards.alice.monolith.owner.services;

import cards.alice.monolith.common.models.BlueprintDto;

import java.util.Optional;
import java.util.Set;

public interface OwnerBlueprintService {
    BlueprintDto saveNewBlueprint(BlueprintDto blueprintDto);

    Optional<BlueprintDto> getBlueprintById(Long id);

    Optional<BlueprintDto> updateBlueprintById(Long id, BlueprintDto blueprintDto);

    Set<BlueprintDto> listBlueprints(Long storeId, Set<Long> ids);
}
