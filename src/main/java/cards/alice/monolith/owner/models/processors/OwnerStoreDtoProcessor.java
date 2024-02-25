package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.BaseEntity;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.models.StoreDto;
import cards.alice.monolith.common.models.processors.StoreDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.*;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerStoreDtoProcessor extends StoreDtoProcessor {
    private final OwnerStoreRepository storeRepository;
    private final Map<String, OwnerMembershipDto> ownerMembershipMap;

    protected void checkMembershipForPost(Collection<StoreDto> dtos) {
        final Set<String> violationMessages = new HashSet<>();

        final UUID ownerId = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        final OwnerMembershipDto highestOwnerMembership = (OwnerMembershipDto) MembershipDto
                .highestPriority(OwnerMembershipDto
                        .getCurrentOwnerMemberships(ownerMembershipMap));

        final Optional<StoreDto> sampleDto = dtos.stream().findAny();
        if (sampleDto.isEmpty()) {
            violationMessages.add("No StoreDto provided");
            throw new DtoProcessingException("Failed to check membership for StoreDtos", violationMessages);
        }

        // @Min(-1) numMaxAccumulatedTotalStores;
        // Includes DELETED stores
        if (highestOwnerMembership.getNumMaxAccumulatedTotalStores() != -1) {
            long numMaxAccumulatedTotal = storeRepository.exclusiveCountByOwnerId(ownerId);
            if (highestOwnerMembership.getNumMaxAccumulatedTotalStores() < numMaxAccumulatedTotal + dtos.size()) {
                violationMessages.add("Exceeded maximum accumulated total stores.");
            }
        }

        // @Min(-1) numMaxCurrentTotalStores;
        // NOT includes DELETED stores
        if (highestOwnerMembership.getNumMaxCurrentTotalStores() != -1) {
            long numMaxCurrentTotal = storeRepository.exclusiveCountByOwnerIdAndIsDeleted(
                    ownerId, Boolean.FALSE);
            if (highestOwnerMembership.getNumMaxCurrentTotalStores() < numMaxCurrentTotal + dtos.size()) {
                violationMessages.add("Exceeded maximum current total stores.");
            }
        }


        // @Min(-1) numMaxCurrentActiveStores;
        // NOT includes DELETED nor INACTIVE stores
        if (highestOwnerMembership.getNumMaxCurrentActiveStores() != -1) {
            long numMaxCurrentActive = storeRepository.exclusiveCountByOwnerIdAndIsDeletedAndIsInactive(
                    ownerId, Boolean.FALSE, Boolean.FALSE);
            if (highestOwnerMembership.getNumMaxCurrentActiveStores() < numMaxCurrentActive + dtos.size()) {
                violationMessages.add("Exceeded maximum current active stores.");
            }
        }

        // @Min(-1) numMaxCurrentTotalBlueprintsPerStore;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveBlueprintsPerStore;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalRedeemRulesPerBlueprint;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveRedeemRulesPerBlueprint;
        // Not relevant

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to check membership for StoreDtos", violationMessages);
        }
    }

    @Override
    protected StoreDto preprocessForPost(StoreDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id
        // Should be null
        // version
        // Ignored in entity
        // @NotNull isDeleted
        // Validated by @Valid
        // Should be FALSE
        dto.preprocessBaseForNew();

        // @NotBlank @Length(max = 30) displayName;
        // Validated by @Valid

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotBlank @Length(max = 1000) description;
        // Validated by @Valid

        // @NotBlank @Length(max = 7) zipcode;
        // Validated by @Valid

        // @NotBlank @Length(max = 120) address;
        // Validated by @Valid

        // @NotBlank @Length(max = 15) phone;
        // Validated by @Valid

        // @DecimalMin("-90.0") @DecimalMax("90.0") @NotNull lat;
        // Validated by @Valid

        // @DecimalMin("-180.0") @DecimalMax("180.0") @NotNull lng;
        // Validated by @Valid

        // @NotNull isClosed;
        // Validated by @Valid
        // Overwrite to FALSE
        dto.setIsClosed(Boolean.FALSE);

        // @NotNull isInactive;
        // Validated by @Valid
        // Overwrite to FALSE
        dto.setIsInactive(Boolean.FALSE);

        // bgImageId;
        // Can be modified
        // TODO: Query storage to check if file exists

        // profileImageId;
        // Can be modified
        // TODO: Query storage to check if file exists

        // @NotNull ownerId;
        // Validated by @Valid
        // Should be current user's name
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(dto.getOwnerId().toString())) {
            violationMessages.add("Unidentified owner ID provided");
        }

        // blueprintDtos;
        // Ignored in input
        dto.setBlueprintDtos(new HashSet<>());

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess StoreDto", violationMessages);
        }

        return dto;
    }

    @Override
    public StoreDto preprocessForPut(Long id, @Valid StoreDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // OwnerStoreRepository authenticates
        // : Owner should own store
        // Exclusive lock: is modification target
        final Store originalStore = storeRepository.exclusiveFindById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Store.class, id));

        // Owner cannot modify closed store
        if (originalStore.getIsClosed()) {
            violationMessages.add("Cannot modify store that is already closed");
        }

        // id
        // Should not be modified
        dto.setId(id);

        // version
        // Ignored in entity

        // @NotBlank @Length(max = 30) displayName;
        // Validated by @Valid
        // Can be modified

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotNull isDeleted
        // Validated by @Valid
        // Owner cannot soft-delete store
        if (dto.getIsDeleted()) {
            violationMessages.add("Owner cannot soft-delete store");
        }

        // @NotBlank @Length(max = 1000) description;
        // Validated by @Valid
        // Can be modified

        // @NotBlank @Length(max = 7) zipcode;
        // Validated by @Valid
        // Can be modified

        // @NotBlank @Length(max = 120) address;
        // Validated by @Valid
        // Can be modified

        // @NotBlank @Length(max = 15) phone;
        // Validated by @Valid
        // Can be modified

        // @DecimalMin("-90.0") @DecimalMax("90.0") @NotNull lat;
        // Validated by @Valid
        // Can be modified

        // @DecimalMin("-180.0") @DecimalMax("180.0") @NotNull lng;
        // Validated by @Valid
        // Can be modified

        // @NotNull isClosed;
        // Validated by @Valid
        // CANNOT shift from TRUE to FALSE
        if (originalStore.getIsClosed() && !dto.getIsClosed()) {
            violationMessages.add("Cannot re-open store that is already closed");
        }
        // To close store, all blueprints must be expired.
        if (dto.getIsClosed()) {
            var now = OffsetDateTime.now();
            var activeBlueprints = originalStore.getBlueprints().stream().filter(blueprint -> blueprint.getExpirationDate().isAfter(now)).toList();
            if (!activeBlueprints.isEmpty()) {
                violationMessages.add("Cannot close store with active blueprints: " + activeBlueprints.stream()
                        .map(BaseEntity::getDisplayName).toList());
            }
        }

        // @NotNull isInactive;
        // Validated by @Valid
        // Overwrite
        dto.setIsInactive(dto.getIsDeleted() || dto.getIsClosed());

        // bgImageId;
        // Can be modified
        // TODO: Query storage to check if file exists

        // profileImageId;
        // Can be modified
        // TODO: Query storage to check if file exists

        // @NotNull ownerId;
        // Validated by @Valid
        // CANNOT be modified
        if (originalStore.getOwnerId().compareTo(dto.getOwnerId()) != 0) {
            violationMessages.add("Owner cannot be changed");
        }

        // blueprintDtos;
        // Ignored in input

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess StoreDto", violationMessages);
        }

        return dto;
    }
}
