package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.mappers.StoreMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
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

    StoreDto legalDto() {
        return StoreDto.builder()
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
    }

    @Test
    @Transactional
    void preprocessForPost() {
        StoreDto dto1 = legalDto();
        StoreDto dto2 = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentActiveStores(6) // 3(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertDoesNotThrow(() -> {
                storeDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostExceedsMaxAccumulatedTotalStores() {
        StoreDto dto1 = legalDto();
        StoreDto dto2 = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(6) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentActiveStores(6) // 3(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                storeDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostExceedsMaxCurrentTotalStores() {
        StoreDto dto1 = legalDto();
        StoreDto dto2 = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalStores(6) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentActiveStores(6) // 3(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                storeDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostExceedsMaxCurrentActiveStores() {
        StoreDto dto1 = legalDto();
        StoreDto dto2 = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalStores(7) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentActiveStores(5) // 3(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                storeDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostSingle() {
        StoreDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(6) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalStores(6) // 4(bootstrap)+1(SetUp)
                            .numMaxCurrentActiveStores(5) // 3(bootstrap)+1(SetUp)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertDoesNotThrow(() -> {
                storeDtoProcessor.preprocessForPostSingle(dto);
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostSingleLatOutOfRange() {
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
            storeDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleLngOutOfRange() {
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
            storeDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleUnknownOwnerId() {
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
            storeDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPut() {
        StoreDto dto = storeMapper.toDto(originalStore);
        assertDoesNotThrow(() -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyClosedStore() {
        originalStore.setIsClosed(true);
        Store saved = storeRepository.save(originalStore);
        StoreDto dto = storeMapper.toDto(saved);
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTrySoftDeleteStore() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setIsDeleted(true);
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
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
    @Transactional
    void preprocessForPutTryModifyIsInactive() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setIsInactive(true);
        StoreDto preprocessedForPut = storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        assertFalse(preprocessedForPut.getIsInactive());
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyOwnerId() {
        StoreDto dto = storeMapper.toDto(originalStore);
        dto.setOwnerId(UUID.randomUUID());
        assertThrows(DtoProcessingException.class, () -> {
            storeDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }
}