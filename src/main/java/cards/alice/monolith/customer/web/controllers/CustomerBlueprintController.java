package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.services.CustomerBlueprintService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerBlueprintController {
    private final CustomerBlueprintService customerBlueprintService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.blueprint}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<BlueprintDto> getBlueprintById(@PathVariable Long id) {
        final Optional<BlueprintDto> blueprintDto = customerBlueprintService.getBlueprintById(id);
        return ResponseEntity.ok(blueprintDto.orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id)));
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.blueprint.list}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Set<BlueprintDto>> listBlueprints(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) List<Long> ids) {
        if (storeId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<BlueprintDto> blueprintDtos = customerBlueprintService.listBlueprints(storeId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(blueprintDtos);
    }

    /**
     * Queries number of TOTAL issue of target blueprint.<br>
     * (Not number of issues of individual cutomer)
     */
    @GetMapping(path = "${cards.alice.customer.web.controllers.path.blueprint.num-issues}")
    public ResponseEntity<Long> getNumIssues(@NotNull @RequestParam Long blueprintId) {
        final Long numIssues = customerBlueprintService.getNumIssues(blueprintId);
        return ResponseEntity.ok(numIssues);
    }
}
