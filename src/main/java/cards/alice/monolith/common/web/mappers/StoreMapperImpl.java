package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StoreMapperImpl implements StoreMapper {
    private final BlueprintMapper blueprintMapper;

    @Override
    public Store toEntity(StoreDto storeDto) {
        if (storeDto == null) {
            return null;
        }

        Store.StoreBuilder<?, ?> store = Store.builder()
                .id(storeDto.getId())
                .version(storeDto.getVersion())
                .displayName(storeDto.getDisplayName())
                .createdDate(storeDto.getCreatedDate())
                .lastModifiedDate(storeDto.getLastModifiedDate())
                .isDeleted(storeDto.getIsDeleted())
                .description(storeDto.getDescription())
                .zipcode(storeDto.getZipcode())
                .address(storeDto.getAddress())
                .phone(storeDto.getPhone())
                .lat(storeDto.getLat())
                .lng(storeDto.getLng())
                .bgImageId(storeDto.getBgImageId())
                .profileImageId(storeDto.getProfileImageId())
                .blueprints(storeDto.getBlueprintDtos().stream()
                        .map(blueprintMapper::toEntity)
                        .collect(Collectors.toSet()));
        return store.build();
    }

    @Override
    public StoreDto toDto(Store store) {
        if (store == null) {
            return null;
        }

        StoreDto.StoreDtoBuilder<?, ?> storeDto = StoreDto.builder()
                .id(store.getId())
                .version(store.getVersion())
                .displayName(store.getDisplayName())
                .createdDate(store.getCreatedDate())
                .lastModifiedDate(store.getLastModifiedDate())
                .isDeleted(store.getIsDeleted())
                .description(store.getDescription())
                .zipcode(store.getZipcode())
                .address(store.getAddress())
                .phone(store.getPhone())
                .lat(store.getLat())
                .lng(store.getLng())
                .bgImageId(store.getBgImageId())
                .profileImageId(store.getProfileImageId())
                .ownerId(store.getOwnerId())
                .blueprintDtos(store.getBlueprints() == null ? null :
                        store.getBlueprints().stream()
                                .map(blueprintMapper::toDto)
                                .collect(Collectors.toSet()));

        return storeDto.build();
    }

    @Override
    public Store partialUpdate(StoreDto storeDto, Store store) {
        {
            if (storeDto == null) {
                return store;
            }

            if (storeDto.getId() != null) {
                store.setId(storeDto.getId());
            }
            if (storeDto.getVersion() != null) {
                store.setVersion(storeDto.getVersion());
            }
            if (storeDto.getDisplayName() != null) {
                store.setDisplayName(storeDto.getDisplayName());
            }
            if (storeDto.getCreatedDate() != null) {
                store.setCreatedDate(storeDto.getCreatedDate());
            }
            if (storeDto.getLastModifiedDate() != null) {
                store.setLastModifiedDate(storeDto.getLastModifiedDate());
            }
            if (storeDto.getIsDeleted() != null) {
                store.setIsDeleted(storeDto.getIsDeleted());
            }
            if (storeDto.getDescription() != null) {
                store.setDescription(storeDto.getDescription());
            }
            if (storeDto.getZipcode() != null) {
                store.setZipcode(storeDto.getZipcode());
            }
            if (storeDto.getAddress() != null) {
                store.setAddress(storeDto.getAddress());
            }
            if (storeDto.getPhone() != null) {
                store.setPhone(storeDto.getPhone());
            }
            if (storeDto.getLat() != null) {
                store.setLat(storeDto.getLat());
            }
            if (storeDto.getLng() != null) {
                store.setLng(storeDto.getLng());
            }
            if (storeDto.getBgImageId() != null) {
                store.setBgImageId(storeDto.getBgImageId());
            }
            if (storeDto.getProfileImageId() != null) {
                store.setProfileImageId(storeDto.getProfileImageId());
            }
            if (storeDto.getOwnerId() != null) {
                store.setOwnerId(storeDto.getOwnerId());
            }
            if (storeDto.getBlueprintDtos() != null) {
                store.setBlueprints(storeDto.getBlueprintDtos().stream()
                        .map(blueprintMapper::toEntity)
                        .collect(Collectors.toSet()));
            }

            return store;
        }
    }
}
