package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.services.OwnerBlueprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerBlueprintController {
    @Value("${cards.alice.owner.server.host}:${cards.alice.owner.server.port}")
    private String ownerHostname;
    @Value("${cards.alice.owner.web.controllers.path.base}${cards.alice.owner.web.controllers.path.blueprint}")
    private String ownerBlueprintPath;

    private final OwnerBlueprintService ownerBlueprintService;

    @PostMapping(path = "${cards.alice.owner.web.controllers.path.blueprint}")
    public ResponseEntity postBlueprint(@Validated @RequestBody BlueprintDto blueprintDto) {
        final BlueprintDto savedBlueprintDto = ownerBlueprintService.saveNewBlueprint(blueprintDto);
        return ResponseEntity.created(URI.create(ownerHostname + ownerBlueprintPath + "/" + savedBlueprintDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.blueprint}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BlueprintDto> getBlueprint(@PathVariable Long id) {
        final Optional<BlueprintDto> blueprintDto = ownerBlueprintService.getBlueprintById(id);
        return ResponseEntity.ok(blueprintDto.orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id)));
    }

    @PutMapping(path = "${cards.alice.owner.web.controllers.path.blueprint}/{id}")
    public ResponseEntity putBlueprint(@PathVariable Long id, @Validated @RequestBody BlueprintDto blueprintDto) {
        Optional<BlueprintDto> updatedBlueprintDto = ownerBlueprintService.updateBlueprintById(id, blueprintDto);
        updatedBlueprintDto.orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.blueprint.list}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Set<BlueprintDto>> listBlueprints(@RequestParam(required = false) Long storeId, @RequestParam(required = false) List<Long> ids) {
        if (storeId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<BlueprintDto> blueprintDtos = ownerBlueprintService.listBlueprints(storeId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(blueprintDtos);
    }
}
