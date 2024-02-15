package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Validated
@RequiredArgsConstructor
public class OwnerBlueprintDtoProcessor implements DtoProcessor<BlueprintDto, Long> {
    private final OwnerBlueprintRepository blueprintRepository;
    private final OwnerStoreRepository storeRepository;

    @Override
    public BlueprintDto preprocessForPost(@Valid BlueprintDto dto) {
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
        final Store originalStore = storeRepository.findById(dto.getStoreId())
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

        final Blueprint originalBlueprint = blueprintRepository.findById(id)
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
        if (dto.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("expirationDate already passed");
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
