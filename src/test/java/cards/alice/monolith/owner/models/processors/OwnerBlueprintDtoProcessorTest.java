package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.StoreRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.BlueprintMapper;
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

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
class OwnerBlueprintDtoProcessorTest {
    @Autowired
    OwnerBlueprintDtoProcessor blueprintDtoProcessor;

    @Autowired
    BlueprintMapper blueprintMapper;

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    BlueprintRepository blueprintRepository;

    Store store;
    Blueprint originalBlueprint;

    @BeforeEach
    void setUp() {
        store = storeRepository.findById(1L).orElseThrow();
        Blueprint blueprint = Blueprint.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(3)
                .numMaxIssuesPerCustomer(5)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .store(store)
                .build();
        originalBlueprint = blueprintRepository.save(blueprint);
    }

    @AfterEach
    void tearDown() {
        store = null;
        blueprintRepository.deleteById(originalBlueprint.getId());
        originalBlueprint = null;
    }

    BlueprintDto legalDto() {
        return BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
    }

    @Test
    @Transactional
    void preprocessForPost() {
        BlueprintDto dto1 = legalDto();
        BlueprintDto dto2 = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(-1)
                            .numMaxCurrentTotalStores(-1)
                            .numMaxCurrentActiveStores(-1)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertDoesNotThrow(() -> {
                blueprintDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    void preprocessForPostBoundToMultipleStores() {
        BlueprintDto dto1 = legalDto();
        dto1.setStoreId(1L);
        BlueprintDto dto2 = legalDto();
        dto2.setStoreId(2L);
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPost(List.of(dto1, dto2));
        });
    }

    @Test
    void preprocessForPostExceedsNumMaxAccumulatedTotalStores() {
        BlueprintDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(-1)
                            .numMaxCurrentTotalStores(-1)
                            .numMaxCurrentActiveStores(-1)
                            .numMaxCurrentTotalBlueprintsPerStore(0)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                blueprintDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    void preprocessForPostExceedsNumMaxCurrentActiveBlueprintsPerStore() {
        BlueprintDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(-1)
                            .numMaxCurrentTotalStores(-1)
                            .numMaxCurrentActiveStores(-1)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(0)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                blueprintDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostSingle() {
        BlueprintDto dto = legalDto();
        try (MockedStatic<MembershipDto> mockedStaticMembershipDto = mockStatic(MembershipDto.class)) {
            mockedStaticMembershipDto.when(() -> MembershipDto.highestPriority(any())).thenReturn(
                    OwnerMembershipDto.builder()
                            .version(0)
                            .displayName("owner-alpha")
                            .createdDate(null)
                            .lastModifiedDate(null)
                            .isDeleted(Boolean.FALSE)
                            .priority(1)
                            .numMaxAccumulatedTotalStores(-1)
                            .numMaxCurrentTotalStores(-1)
                            .numMaxCurrentActiveStores(-1)
                            .numMaxCurrentTotalBlueprintsPerStore(-1)
                            .numMaxCurrentActiveBlueprintsPerStore(-1)
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(-1)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertDoesNotThrow(() -> {
                blueprintDtoProcessor.preprocessForPostSingle(dto);
            });
        }
    }

    @Test
    @Transactional
    void preprocessForPostSingleLongDisplayName() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name Dummy Display Name Dummy Display Name Dummy Display Name ")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }


    @Test
    @Transactional
    void preprocessForPostSingleNegativeNumMaxIssues() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(-1)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleNegativeNumMaxStamps() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(-1)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleZeroNumMaxRedeems() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(0)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleZeroNumMaxIssuesPerCustomer() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(0)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleExpired() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().minusSeconds(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleNullIsPublishing() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(null)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleStoreNotFound() {
        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(10L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPostSingleStoreInactive() {
        Store originalStore = storeRepository.findById(1L).orElseThrow();
        originalStore.setIsInactive(true);
        storeRepository.save(originalStore);

        BlueprintDto dto = BlueprintDto.builder()
                .id(-1L)
                .version(-1)
                .displayName("Dummy Display Name")
                .isDeleted(true)
                .description("This is dummy description.")
                .stampGrantCondDescription("This is dummy stamp grant description.")
                .numMaxStamps(10)
                .numMaxRedeems(1)
                .numMaxIssuesPerCustomer(1)
                .numMaxIssues(0)
                .expirationDate(OffsetDateTime.now().plusDays(1L))
                .bgImageId(null)
                .isPublishing(true)
                .storeDto(null)
                .storeId(1L)
                .redeemRuleDtos(null)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPut() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        assertDoesNotThrow(() -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyExpiredBlueprint() {
        originalBlueprint.setExpirationDate(OffsetDateTime.now().minusSeconds(1L));
        blueprintRepository.save(originalBlueprint);
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setExpirationDate(OffsetDateTime.now());
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }


    @Test
    @Transactional
    void preprocessForPutLongDisplayName() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setDisplayName("Dummy Display Name Dummy Display Name Dummy Display Name Dummy Display Name ");
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTrySoftDelete() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setIsDeleted(true);
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutLongDescription() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setDescription("""
                Eiusmod cupidatat qui et et ea. Magna occaecat proident excepteur sint sunt velit non anim nulla amet id. Tempor consectetur est dolor cupidatat aute id pariatur. Ea mollit laboris amet proident voluptate nostrud nisi esse elit aliqua enim fugiat qui nostrud.

                Cillum ullamco sint enim adipisicing eu consequat quis. Id elit nulla nisi id anim pariatur nostrud deserunt. Tempor culpa culpa magna eiusmod incididunt. Est exercitation in do eu amet eiusmod quis. Veniam voluptate laboris minim velit id do voluptate consectetur voluptate et sint elit fugiat. Eu commodo proident commodo incididunt consequat commodo consequat excepteur.

                Voluptate aute excepteur esse nostrud incididunt excepteur consectetur irure officia. Incididunt ad amet ullamco amet laboris qui ex dolor. Ea consectetur ea velit quis laboris veniam dolore officia id adipisicing.

                Sit occaecat adipisicing cillum aliquip sunt voluptate eiusmod ullamco. Ullamco enim incididunt elit nulla ipsum excepteur nisi exercitation aliqua ut. Nulla magna duis ad eiusmod deserunt id occaecat qui. Id est ut sint tempor reprehenderit anim exercitation.""");
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyStampGrantCondDescription() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        // 101 chars
        dto.setDescription("This should be ignored.");
        assertDoesNotThrow(() -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutNegativeNumMaxIssues() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setNumMaxIssues(-1);
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutDecreaseNumMaxStamps() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setNumMaxStamps(originalBlueprint.getNumMaxStamps() - 1);
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutDecreaseNumMaxRedeems() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setNumMaxRedeems(originalBlueprint.getNumMaxRedeems() - 1);
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutZeroNumMaxIssuesPerCustomer() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setNumMaxIssuesPerCustomer(0);
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutExpBeforeCur() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setExpirationDate(OffsetDateTime.now().minusSeconds(1L));
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutExpBeforeRetention() {
        originalBlueprint.setExpirationDate(OffsetDateTime.now().plusDays(8));
        Blueprint saved = blueprintRepository.save(originalBlueprint);
        BlueprintDto dto = blueprintMapper.toDto(saved);
        dto.setExpirationDate(OffsetDateTime.now().plusDays(6));
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutNullIsPublishing() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setIsPublishing(null);
        assertThrows(ConstraintViolationException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    void preprocessForPutTryModifyStore() {
        BlueprintDto dto = blueprintMapper.toDto(originalBlueprint);
        dto.setStoreId(2L);
        assertThrows(DtoProcessingException.class, () -> {
            blueprintDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }
}