package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.MembershipDto;
import cards.alice.monolith.common.models.OwnerMembershipDto;
import cards.alice.monolith.common.models.processors.BlueprintDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerBlueprintDtoProcessor extends BlueprintDtoProcessor {
    @Value("${cards.alice.owner.blueprint.modify-blueprint-exp-date-min-remaining-from-now-in-days}")
    private long modifyBlueprintExpDateMinRemainingFromNowInDays;
    private final OwnerBlueprintRepository blueprintRepository;
    private final OwnerStoreRepository storeRepository;
    private final Map<String, OwnerMembershipDto> ownerMembershipMap;

    @Override
    public Collection<BlueprintDto> preprocessForPost(Collection<BlueprintDto> dtos) {
        checkBoundToSingleStore(dtos);
        return super.preprocessForPost(dtos);
    }

    private void checkBoundToSingleStore(Collection<BlueprintDto> dtos) {
        final Set<Long> ids = dtos.stream().map(BlueprintDto::getStoreId).collect(Collectors.toSet());
        if (ids.contains(null) || ids.size() != 1) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDtos",
                    Set.of("BlueprintDtos bound to multiple stores: " + ids));
        }
    }

    @Override
    protected void checkMembershipForPost(Collection<BlueprintDto> dtos) {
        final Set<String> violationMessages = new HashSet<>();

        final UUID ownerId = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        final OwnerMembershipDto highestOwnerMembership = (OwnerMembershipDto) MembershipDto
                .highestPriority(OwnerMembershipDto
                        .getCurrentOwnerMemberships(ownerMembershipMap));

        final Optional<BlueprintDto> sampleDto = dtos.stream().findAny();
        if (sampleDto.isEmpty()) {
            violationMessages.add("No BlueprintDto provided");
            throw new DtoProcessingException("Failed to check membership for BlueprintDtos", violationMessages);
        }

        final Long storeId = sampleDto.get().getStoreId();

        // @Min(-1) numMaxAccumulatedTotalStores;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalStores;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveStores;
        // Not relevant

        // @Min(-1) numMaxCurrentTotalBlueprintsPerStore;
        // NOT includes DELETED blueprints
        if (highestOwnerMembership.getNumMaxCurrentTotalBlueprintsPerStore() != -1) {
            long numCurrentTotalPerStore = blueprintRepository.exclusiveCountByStore_IdAndStore_OwnerIdAndIsDeleted(
                    storeId, ownerId, Boolean.FALSE);
            if (highestOwnerMembership.getNumMaxCurrentTotalBlueprintsPerStore() < numCurrentTotalPerStore + dtos.size()) {
                violationMessages.add("Exceeded maximum current total blueprints of store.");
            }
        }

        // @Min(-1) numMaxCurrentActiveBlueprintsPerStore;
        // Apply ONLY when creating ACTIVE blueprint
        final List<BlueprintDto> publishingDtos = dtos.stream().filter(BlueprintDto::getIsPublishing).toList();
        // NOT includes DELETED nor INACTIVE blueprints
        if (!publishingDtos.isEmpty()
                && highestOwnerMembership.getNumMaxCurrentActiveBlueprintsPerStore() != -1) {
            long numCurrentTotalPerStore = blueprintRepository.exclusiveCountByStore_IdAndStore_OwnerIdAndIsDeletedAndIsPublishing(
                    storeId, ownerId, Boolean.FALSE, Boolean.TRUE);
            if (highestOwnerMembership.getNumMaxCurrentTotalBlueprintsPerStore() < numCurrentTotalPerStore + publishingDtos.size()) {
                violationMessages.add("Exceeded maximum current total blueprints of store.");
            }
        }

        // @Min(-1) numMaxCurrentTotalRedeemRulesPerBlueprint;
        // Not relevant

        // @Min(-1) numMaxCurrentActiveRedeemRulesPerBlueprint;
        // Not relevant

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to check membership for BlueprintDtos", violationMessages);
        }
    }

    @Override
    protected BlueprintDto preprocessForPost(BlueprintDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id
        // Should be null
        // version
        // Ignored in entity
        // @NotNull isDeleted
        // Validated by @Valid
        // Should be false
        dto.preprocessBaseForNew();

        // @NotBlank @Length(max = 30) displayName;
        // Validated by @Valid
        // Can be modified

        // createdDate: ignored

        // lastModifiedDate: ignored

        // @NotBlank @Length(max = 1000) description
        // Validated by @Valid

        // @NotBlank @Length(max = 100) stampGrantCondDescription
        // Validated by @Valid

        // @NotNull @PositiveOrZero numMaxStamps
        // Validated by @Valid

        // @NotNull @Positive numMaxRedeems
        // Validated by @Valid

        // @NotNull @Positive numMaxIssuesPerCustomer
        // Validated by @Valid

        // @NotNull @PositiveOrZero numMaxIssues
        // Validated by @Valid

        // @NotNull expirationDate
        // Validated by @Valid
        // Should be AFTER now
        if (dto.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("expirationDate already passed");
        }

        // bgImageId
        // TODO: Query storage to check if file exists

        // @NotNull isPublishing
        // Validated by @Valid

        // storeDto
        // Ignored in preprocess

        // storeId
        // Store must exist
        // Exclusive lock: blueprint count could be updated
        final Store originalStore = storeRepository.exclusiveFindById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(Store.class, dto.getStoreId()));
        // Owner cannot create blueprint of inactive store
        if (originalStore.getIsInactive()) {
            violationMessages.add("Owner cannot create blueprint of inactive store");
        }

        // redeemRuleDtos
        // Ignored in input
        // TODO: Should we do this for every one-to-many field?
        dto.setRedeemRuleDtos(new HashSet<>());

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }

    @Override
    public BlueprintDto preprocessForPut(Long id, @Valid BlueprintDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // Exclusive lock: is modification target
        final Blueprint originalBlueprint = blueprintRepository.exclusiveFindById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id));

        // Owner cannot modify expired blueprint
        if (originalBlueprint.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("Owner cannot modify expired blueprint");
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
        // Owner cannot soft-delete
        if (dto.getIsDeleted()) {
            violationMessages.add("Owner cannot soft-delete blueprint");
        }

        // @NotBlank @Length(max = 1000) description
        // Validated by @Valid
        // Can be modified

        // @NotBlank @Length(max = 100) stampGrantCondDescription
        // Validated by @Valid
        // CANNOT be modified
        dto.setStampGrantCondDescription(
                originalBlueprint.getStampGrantCondDescription());

        // @NotNull @PositiveOrZero numMaxStamps
        // Validated by @Valid
        // Can ONLY be increased
        if (dto.getNumMaxStamps() < originalBlueprint.getNumMaxStamps()) {
            violationMessages.add(
                    "BlueprintDto.numMaxStamp(" + dto.getNumMaxStamps() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxStamp(" + originalBlueprint.getNumMaxStamps() + ")");
        }

        // @NotNull @Positive numMaxRedeems
        // Validated by @Valid
        // Can ONLY be increased
        if (dto.getNumMaxRedeems() < originalBlueprint.getNumMaxRedeems()) {
            violationMessages.add(
                    "BlueprintDto.numMaxRedeems(" + dto.getNumMaxRedeems() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxRedeems(" + originalBlueprint.getNumMaxRedeems() + ")");
        }

        // @NotNull @Positive numMaxIssuesPerCustomer
        // Validated by @Valid
        // Can be modified

        // @NotNull @PositiveOrZero numMaxIssues
        // Validated by @Valid
        // Can be modified

        // @NotNull expirationDate
        // Validated by @Valid
        // Should be AFTER now
        OffsetDateTime curExpirationDate = originalBlueprint.getExpirationDate();
        OffsetDateTime sevenDaysAfterNow = OffsetDateTime.now().plusDays(modifyBlueprintExpDateMinRemainingFromNowInDays);
        OffsetDateTime firstDateTime = curExpirationDate.isBefore(sevenDaysAfterNow) ? curExpirationDate : sevenDaysAfterNow;
        if (dto.getExpirationDate().isBefore(firstDateTime)) {
            violationMessages.add("expirationDate must be after " + firstDateTime);
        }

        // bgImageId
        // Can be modified
        // TODO: Query storage to check if file exists

        // @NotNull isPublishing
        // Validated by @Valid
        // Can be modified

        // storeDto
        // Ignored in preprocess

        // storeId
        // CANNOT be modified
        if (!Objects.equals(originalBlueprint.getStore().getId(), dto.getStoreId())) {
            violationMessages.add("Store cannot be changed");
        }

        // redeemRuleDtos
        // Ignored in input
        // TODO: Should we do this for every one-to-many field?
        dto.setRedeemRuleDtos(new HashSet<>());

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }
}
