package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner"})
@ActiveProfiles({"default", "common", "dev", "monolith", "h2", "oauth2_https", "bootstrap"})
class OwnerStoreDtoProcessorTest {
    @Autowired
    OwnerStoreDtoProcessor storeDtoProcessor;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreMapper storeMapper;

    Store originalStore;

    @BeforeEach
    void setUp() {
        Store store = Store.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .zipcode("1234567")
                .address("Baz 3-16-1, Bar District, Foo City, Japan")
                .phone("+8101012345678")
                .lat(BigDecimal.valueOf(90.0))
                .lng(BigDecimal.valueOf(180.0))
                .isClosed(false)
                .isInactive(false)
                .ownerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .build();
        originalStore = storeRepository.save(store);
    }

    @AfterEach
    void tearDown() {
        storeRepository.deleteById(originalStore.getId());
        originalStore = null;
    }

    @Test
    void preprocessForPost() {
        StoreDto dto = StoreDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .zipcode("1234567")
                .address("Baz 3-16-1, Bar District, Foo City, Japan")
                .phone("+8101012345678")
                .lat(BigDecimal.valueOf(90.0))
                .lng(BigDecimal.valueOf(180.0))
                .isClosed(true)
                .isInactive(true)
                .ownerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .build();
        assertDoesNotThrow(() -> {
            storeDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPostLatOutOfRange() {
        StoreDto dto = StoreDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .zipcode("1234567")
                .address("Baz 3-16-1, Bar District, Foo City, Japan")
                .phone("+8101012345678")
                .lat(BigDecimal.valueOf(90.000001))
                .lng(BigDecimal.valueOf(180.0))
                .isClosed(true)
                .isInactive(true)
                .ownerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            storeDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPostLngOutOfRange() {
        StoreDto dto = StoreDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .zipcode("1234567")
                .address("Baz 3-16-1, Bar District, Foo City, Japan")
                .phone("+8101012345678")
                .lat(BigDecimal.valueOf(90))
                .lng(BigDecimal.valueOf(-180.000001))
                .isClosed(true)
                .isInactive(true)
                .ownerId(UUID.fromString("de36b13b-2397-445e-89cd-8e817e0f441e"))
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            storeDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPostUnknownOwnerId() {
        StoreDto dto = StoreDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .zipcode("1234567")
                .address("Baz 3-16-1, Bar District, Foo City, Japan")
                .phone("+8101012345678")
                .lat(BigDecimal.valueOf(90.0))
                .lng(BigDecimal.valueOf(180.0))
                .isClosed(true)
                .isInactive(true)
                .ownerId(UUID.fromString("de36b13b-0000-445e-89cd-8e817e0f441e"))
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPost(dto);
        });
    }

    @Test
    void preprocessForPut() {
        StoreDto dto = storeMapper.toDto(originalStore);
        assertDoesNotThrow(() -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    void preprocessForPutTryModifyClosedStore() {
        originalStore.setIsClosed(true);
        Store saved = storeRepository.save(originalStore);
        StoreDto dto = storeMapper.toDto(saved);
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    void preprocessForPutTrySoftDeleteStore() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setIsDeleted(true);
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    void preprocessForPutTrySetIsClosedFalse() {
        originalStore.setIsClosed(true);
        Store saved = storeRepository.save(originalStore);
        StoreDto dto = storeMapper.toDto(saved);
        dto.setIsClosed(false);
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    void preprocessForPutTryModifyIsInactive() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setIsInactive(true);
        StoreDto preprocessedForPut = storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        assertFalse(preprocessedForPut.getIsInactive());
    }

    @Test
    void preprocessForPutTryModifyOwnerId() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setOwnerId(UUID.randomUUID());
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }
}