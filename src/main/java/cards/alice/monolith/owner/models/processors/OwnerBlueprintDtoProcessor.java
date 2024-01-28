package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OwnerBlueprintDtoProcessor implements DtoProcessor<BlueprintDto, Long> {
    private final OwnerBlueprintRepository blueprintRepository;
    private final OwnerStoreRepository storeRepository;

    @Override
    public BlueprintDto preprocessForPost(BlueprintDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id, version, isDeleted
        dto.preprocessBaseForNew();

        // description, stampGrantCondDescription, numMaxStamps, numMaxRedeems, numMaxIssues
        // Validated by postBlueprint(@Validated BlueprintDto)

        // expirationDate must be after now
        if (dto.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("expirationDate already passed");
        }

        // bgImageId
        // Can be null

        // isPublishing
        // Validated by postBlueprint(@Validated BlueprintDto)

        // storeId must exist
        storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(Store.class, dto.getStoreId()));

        // redeemRuleDtos
        // Can be null

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }

    @Override
    public BlueprintDto preprocessForPut(Long id, BlueprintDto dto) {
        final Set<String> violationMessages = new HashSet<>();

        // id, version
        dto.setIdVersionNull();

        // isDeleted
        // Validated by @Validated BlueprintDto

        // description
        // Validated by postBlueprint(@Validated BlueprintDto)

        // Owner cannot control stampGrantCondDescription
        dto.setStampGrantCondDescription(null);

        // Owner can only increase numMaxStamps, numMaxRedeems
        final Blueprint blueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id));
        if (dto.getNumMaxStamps() <= blueprint.getNumMaxStamps()) {
            violationMessages.add(
                    "BlueprintDto.numMaxStamp(" + dto.getNumMaxStamps() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxStamp(" + blueprint.getNumMaxStamps() + ")");
        }
        if (dto.getNumMaxRedeems() <= blueprint.getNumMaxRedeems()) {
            violationMessages.add(
                    "BlueprintDto.numMaxRedeems(" + dto.getNumMaxRedeems() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxRedeems(" + blueprint.getNumMaxRedeems() + ")");
        }

        // numMaxIssues
        // Can increase or decrease

        // expirationDate must be after now
        if (dto.getExpirationDate().isBefore(OffsetDateTime.now())) {
            violationMessages.add("expirationDate already passed");
        }

        // bgImageId
        // Can be null

        // isPublishing
        // Validated by postBlueprint(@Validated BlueprintDto)

        // storeId must exist
        storeRepository.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(Store.class, dto.getStoreId()));

        // redeemRuleDtos
        // Can be null

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }
}
