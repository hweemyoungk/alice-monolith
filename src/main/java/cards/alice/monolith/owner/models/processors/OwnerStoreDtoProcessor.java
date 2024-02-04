package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.models.processors.StoreDtoProcessor;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerStoreDtoProcessor implements StoreDtoProcessor {
    private final OwnerStoreRepository storeRepository;

    @Override
    public StoreDto preprocessForPost(StoreDto dto) {
        // id, version, isDeleted
        dto.preprocessBaseForNew();

        // description, zipcode, address, phone, lat, lng
        // Validated by postStore(@RequestBody StoreDto)

        // bgImageId, profileImageId
        // TODO: Query storage to check if file exists

        // ownerId must exist
        // Validated by @PreAuthorize postCard

        // blueprintDtos
        // can be null

        return dto;
    }

    @Override
    public StoreDto preprocessForPut(Long id, StoreDto dto) {
        // Authenticate
        storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Store.class, id));

        // id, version
        dto.setIdVersionNull();

        // isDeleted, description, zipcode, address, phone, lat, lng
        // Validated by putStore(..., @RequestBody StoreDto)

        // bgImageId, profileImageId
        // TODO: Query storage to check if file exists

        // ownerId must exist
        // Validated by @PreAuthorize putStore

        // blueprintDtos
        // can be null

        return dto;
    }
}
