package cards.alice.monolith.customer.web.controllers;

import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.customer.services.CustomerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("${cards.alice.customer.web.controllers.path.base}")
@RequiredArgsConstructor
public class CustomerStoreController {
    private final CustomerStoreService customerStoreService;

    @GetMapping(path = "${cards.alice.customer.web.controllers.path.store.list}")
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
