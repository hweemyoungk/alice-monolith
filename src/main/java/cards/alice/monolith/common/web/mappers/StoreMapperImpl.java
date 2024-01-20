package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StoreMapperImpl implements StoreMapper {
    private final DateMapper dateMapper;
    private final BlueprintMapper blueprintMapper;

    @Override
    public Store toEntity(StoreDto storeDto) {
        if (storeDto == null) {
            return null;
        }

        Store.StoreBuilder store = Store.builder()
                .id(storeDto.getId())
                .version(storeDto.getVersion())
                .displayName(storeDto.getDisplayName())
                .createdDate(dateMapper.asTimestamp(storeDto.getCreatedDate()))
                .lastModifiedDate(dateMapper.asTimestamp(storeDto.getLastModifiedDate()))
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

        StoreDto.StoreDtoBuilder storeDto = StoreDto.builder()
                .id(store.getId())
                .version(store.getVersion())
                .displayName(store.getDisplayName())
                .createdDate(dateMapper.asOffsetDateTime(store.getCreatedDate()))
                .lastModifiedDate(dateMapper.asOffsetDateTime(store.getLastModifiedDate()))
                .isDeleted(store.getIsDeleted())
                .description(store.getDescription())
                .zipcode(store.getZipcode())
                .address(store.getAddress())
                .phone(store.getPhone())
                .lat(store.getLat())
                .lng(store.getLng())
                .bgImageId(store.getBgImageId())
                .profileImageId(store.getProfileImageId());

        return storeDto.build();
    }
}
