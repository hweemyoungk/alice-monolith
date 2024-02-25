package cards.alice.monolith.owner.web.controllers;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.services.OwnerStoreService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("${cards.alice.owner.web.controllers.path.base}")
@RequiredArgsConstructor
public class OwnerStoreController {
    @Value("${cards.alice.owner.server.host}:${cards.alice.owner.server.port}")
    private String ownerHostname;
    @Value("${cards.alice.owner.web.controllers.path.base}${cards.alice.owner.web.controllers.path.store}")
    private String ownerStorePath;

    private final OwnerStoreService ownerStoreService;

    @PostMapping(path = "${cards.alice.owner.web.controllers.path.store}")
    @PreAuthorize("authentication.name == #storeDto.ownerId.toString()")
    public ResponseEntity postStore(@RequestBody StoreDto storeDto) {
        final StoreDto savedStoreDto = ownerStoreService.saveNewStore(storeDto);
        return ResponseEntity.created(URI.create(ownerHostname + ownerStorePath + "/" + savedStoreDto.getId())).build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store}/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<StoreDto> getStore(@PathVariable Long id) {
        final Optional<StoreDto> storeDto = ownerStoreService.getStoreById(id);
        return ResponseEntity.ok(storeDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id)));
    }

    @PutMapping(path = "${cards.alice.owner.web.controllers.path.store}/{id}")
    @PreAuthorize("authentication.name == #storeDto.ownerId.toString()")
    //public ResponseEntity putStore(@PathVariable Long id, @Validated @RequestBody StoreDto storeDto) {
    public ResponseEntity putStore(@PathVariable Long id, @RequestBody StoreDto storeDto) {
        Optional<StoreDto> updatedStoreDto = ownerStoreService.updateStoreById(id, storeDto);
        updatedStoreDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "${cards.alice.owner.web.controllers.path.store}/{id}")
    public ResponseEntity closeStore(@PathVariable Long id) {
        final Optional<StoreDto> storeDto = ownerStoreService.closeStoreById(id);
        storeDto.orElseThrow(() -> new ResourceNotFoundException(Store.class, id));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store.list}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("#ownerId == null ? true : authentication.name == #ownerId.toString()")
    public ResponseEntity<Set<StoreDto>> listStores(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) List<Long> ids) {
        if (ownerId == null && CollectionUtils.isEmpty(ids)) {
            return ResponseEntity.badRequest().build();
        }
        final Set<StoreDto> storeDtos = ownerStoreService.listStores(ownerId, ids == null ? null : new HashSet<>(ids));
        return ResponseEntity.ok(storeDtos);
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store.num-accumulated-total-stores}")
    @PreAuthorize("authentication.name == #ownerId.toString()")
    public ResponseEntity<Long> getNumAccumulatedTotalStores(@NotNull @RequestParam UUID ownerId) {
        final Long numAccumulatedTotalStores = ownerStoreService.getNumAccumulatedTotalStores(ownerId);
        return ResponseEntity.ok(numAccumulatedTotalStores);
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store.num-current-total-stores}")
    @PreAuthorize("authentication.name == #ownerId.toString()")
    public ResponseEntity<Long> getNumCurrentTotalStores(@NotNull @RequestParam UUID ownerId) {
        final Long numCurrentTotalStores = ownerStoreService.getNumCurrentTotalStores(ownerId);
        return ResponseEntity.ok(numCurrentTotalStores);
    }

    @GetMapping(path = "${cards.alice.owner.web.controllers.path.store.num-current-active-stores}")
    @PreAuthorize("authentication.name == #ownerId.toString()")
    public ResponseEntity<Long> getNumCurrentActiveStores(@NotNull @RequestParam UUID ownerId) {
        final Long numCurrentActiveStores = ownerStoreService.getNumCurrentActiveStores(ownerId);
        return ResponseEntity.ok(numCurrentActiveStores);
    }
}
