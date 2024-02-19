package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.RedeemRule;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.models.RedeemDto;
import cards.alice.monolith.common.models.RedeemRuleDto;
import cards.alice.monolith.common.repositories.BlueprintRepository;
import cards.alice.monolith.common.repositories.RedeemRuleRepository;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.common.web.mappers.RedeemRuleMapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.SmartValidator;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
@WithMockUser(
        username = "de36b13b-2397-445e-89cd-8e817e0f441e",
        roles = {"owner-alpha"})
@ActiveProfiles({"default", "local", "bootstrap"})
class OwnerRedeemRuleDtoProcessorTest {
    @Autowired
    OwnerRedeemRuleDtoProcessor redeemRuleDtoProcessor;

    @Autowired
    RedeemRuleMapper redeemRuleMapper;

    @Autowired
    BlueprintRepository blueprintRepository;

    @Autowired
    RedeemRuleRepository redeemRuleRepository;

    @Autowired
    SmartValidator validator;

    Blueprint blueprint;
    RedeemRule originalRedeemRule;

    @BeforeEach
    void setUp() {
        blueprint = blueprintRepository.findById(1L).orElseThrow();
        RedeemRule redeemRule = RedeemRule.builder()
                .displayName("Rule 3")
                .description("This is Rule 3")
                .consumes(10)
                .imageId(null)
                .blueprint(blueprint)
                .build();
        originalRedeemRule = redeemRuleRepository.save(redeemRule);
    }

    @AfterEach
    void tearDown() {
        blueprint = null;
        redeemRuleRepository.deleteById(originalRedeemRule.getId());
        originalRedeemRule = null;
    }

    private RedeemRuleDto legalDto() {
        RedeemDto redeemDto = RedeemDto.builder().build();
        return RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .consumes(blueprint.getNumMaxStamps())
                .blueprintId(1L)
                .redeemDtos(Set.of(redeemDto))
                .build();
    }

    @Test
    @Transactional
    void preprocessForPost() {
        RedeemRuleDto dto1 = legalDto();
        RedeemRuleDto dto2 = legalDto();
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
                redeemRuleDtoProcessor.preprocessForPost(List.of(dto1, dto2));
            });
        }
    }

    @Test
    void preprocessForPostBoundToMultipleStores() {
        RedeemRuleDto dto1 = legalDto();
        dto1.setBlueprintId(1L);
        RedeemRuleDto dto2 = legalDto();
        dto2.setBlueprintId(2L);
        assertThrows(DtoProcessingException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPost(List.of(dto1, dto2));
        });
    }

    @Test
    void preprocessForPostExceedsNumMaxAccumulatedTotalStores() {
        RedeemRuleDto dto = legalDto();
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
                            .numMaxCurrentTotalRedeemRulesPerBlueprint(0)
                            .numMaxCurrentActiveRedeemRulesPerBlueprint(-1)
                            .build()
            );
            assertThrows(DtoProcessingException.class, () -> {
                redeemRuleDtoProcessor.preprocessForPost(List.of(dto));
            });
        }
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingle() {
        RedeemRuleDto dto = legalDto();
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
                redeemRuleDtoProcessor.preprocessForPostSingle(dto);
            });
        }
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleLongDisplayName() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name Dummy Display Name Dummy Display Name ")
                .isDeleted(false)
                .description("This is dummy description.")
                .consumes(blueprint.getNumMaxStamps())
                .blueprintId(1L)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleNullDeleted() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(null)
                .description("This is dummy description.")
                .consumes(blueprint.getNumMaxStamps())
                .blueprintId(1L)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleLongDescription() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("""
                        Eiusmod cupidatat qui et et ea. Magna occaecat proident excepteur sint sunt velit non anim nulla amet id. Tempor consectetur est dolor cupidatat aute id pariatur. Ea mollit laboris amet proident voluptate nostrud nisi esse elit aliqua enim fugiat qui nostrud.

                        Cillum ullamco sint enim adipisicing eu consequat quis. Id elit nulla nisi id anim pariatur nostrud deserunt. Tempor culpa culpa magna eiusmod incididunt. Est exercitation in do eu amet eiusmod quis. Veniam voluptate laboris minim velit id do voluptate consectetur voluptate et sint elit fugiat. Eu commodo proident commodo incididunt consequat commodo consequat excepteur.

                        Voluptate aute excepteur esse nostrud incididunt excepteur consectetur irure officia. Incididunt ad amet ullamco amet laboris qui ex dolor. Ea consectetur ea velit quis laboris veniam dolore officia id adipisicing.

                        Sit occaecat adipisicing cillum aliquip sunt voluptate eiusmod ullamco. Ullamco enim incididunt elit nulla ipsum excepteur nisi exercitation aliqua ut. Nulla magna duis ad eiusmod deserunt id occaecat qui. Id est ut sint tempor reprehenderit anim exercitation.""")
                .consumes(blueprint.getNumMaxStamps())
                .blueprintId(1L)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleNegativeConsumes() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .consumes(-1)
                .blueprintId(1L)
                .build();
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleConsumesOverMaxStamp() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .consumes(blueprint.getNumMaxStamps() + 1)
                .blueprintId(1L)
                .build();
        assertThrows(DtoProcessingException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPostSingleBlueprintNotFound() {
        RedeemRuleDto dto = RedeemRuleDto.builder()
                .displayName("Dummy Display Name")
                .isDeleted(false)
                .description("This is dummy description.")
                .consumes(blueprint.getNumMaxStamps())
                .blueprintId(10L)
                .build();
        assertThrows(ResourceNotFoundException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPostSingle(dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPut() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        assertDoesNotThrow(() -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutLongDisplayName() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setDisplayName("Dummy Display Name Dummy Display Name Dummy Display Name ");
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutNullDeleted() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setIsDeleted(null);
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutTrySoftDelete() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setIsDeleted(true);
        assertThrows(DtoProcessingException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutLongDescription() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setDescription("""
                Eiusmod cupidatat qui et et ea. Magna occaecat proident excepteur sint sunt velit non anim nulla amet id. Tempor consectetur est dolor cupidatat aute id pariatur. Ea mollit laboris amet proident voluptate nostrud nisi esse elit aliqua enim fugiat qui nostrud.

                Cillum ullamco sint enim adipisicing eu consequat quis. Id elit nulla nisi id anim pariatur nostrud deserunt. Tempor culpa culpa magna eiusmod incididunt. Est exercitation in do eu amet eiusmod quis. Veniam voluptate laboris minim velit id do voluptate consectetur voluptate et sint elit fugiat. Eu commodo proident commodo incididunt consequat commodo consequat excepteur.

                Voluptate aute excepteur esse nostrud incididunt excepteur consectetur irure officia. Incididunt ad amet ullamco amet laboris qui ex dolor. Ea consectetur ea velit quis laboris veniam dolore officia id adipisicing.

                Sit occaecat adipisicing cillum aliquip sunt voluptate eiusmod ullamco. Ullamco enim incididunt elit nulla ipsum excepteur nisi exercitation aliqua ut. Nulla magna duis ad eiusmod deserunt id occaecat qui. Id est ut sint tempor reprehenderit anim exercitation.""");
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutNegativeConsumes() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setConsumes(-1);
        assertThrows(ConstraintViolationException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutConsumesOverMaxStamp() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setConsumes(blueprint.getNumMaxStamps() + 1);
        assertThrows(DtoProcessingException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }

    @Test
    @Transactional
    @Rollback
    void preprocessForPutBlueprintNotFound() {
        RedeemRuleDto dto = redeemRuleMapper.toDto(originalRedeemRule);
        dto.setBlueprintId(10L);
        assertThrows(DtoProcessingException.class, () -> {
            redeemRuleDtoProcessor.preprocessForPut(dto.getId(), dto);
        });
    }
}