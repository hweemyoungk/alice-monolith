package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.services.CustomerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerStoreController {
    private final CustomerStoreService customerStoreService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.store}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StoreDto> getStore(@PathVariable Long id) {
        final Optional<StoreDto> storeDto = customerStoreService.getStoreById(id);
        return ResponseEntity.ok(storeDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id)));
    }

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.store.list}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Set<StoreDto>> listStores(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) List<Long> ids) {
        if (ownerId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<StoreDto> storeDtos = customerStoreService.listStores(ownerId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(storeDtos);
    }
}
