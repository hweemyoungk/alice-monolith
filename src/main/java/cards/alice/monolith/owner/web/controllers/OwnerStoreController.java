package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.services.OwnerStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerStoreController {
    @Value("${cards.alice.owner.server.host}:${cards.alice.owner.server.port}")
    private String ownerHostname;
    @Value("${cards.alice.owner.web.controllers.path.base}/${cards.alice.owner.web.controllers.path.store}")
    private String ownerStorePath;

    private final OwnerStoreService ownerStoreService;


    @PostMapping(path = "${cards.alice.owner.web.controllers.path.store}")
    public ResponseEntity postStore(@RequestBody StoreDto storeDto) {
        final StoreDto savedStoreDto = ownerStoreService.saveNewStore(storeDto);
        return ResponseEntity.created(URI.create(ownerHostname + ownerStorePath + "/" + savedStoreDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store}/{id}")
    public ResponseEntity<StoreDto> getStore(@PathVariable Long id) {
        final Optional<StoreDto> storeDto = ownerStoreService.getStoreById(id);
        return ResponseEntity.ok(storeDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id)));
    }

    @PutMapping(path = "${cards.alice.owner.web.controllers.path.store}/{id}")
    public ResponseEntity putStore(@PathVariable Long id, @Validated @RequestBody StoreDto storeDto) {
        Optional<StoreDto> updatedStoreDto = ownerStoreService.updateStoreById(id, storeDto);
        updatedStoreDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store.list}")
    public ResponseEntity<Set<StoreDto>> listStores(@RequestParam UUID ownerId, @RequestParam List<Long> ids) {
        if (ownerId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<StoreDto> storeDtos = ownerStoreService.listStores(ownerId, new HashSet<>(ids));
        return ResponseEntity.ok(storeDtos);
    }
}
