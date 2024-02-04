package cards.alice.monolith.owner.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.domain.Store;
import cards.alice.monolith.common.models.BlueprintDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.models.processors.RedeemRuleDtoProcessor;
import cards.alice.monolith.common.web.exceptions.DtoProcessingException;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.owner.repositories.OwnerBlueprintRepository;
import cards.alice.monolith.owner.repositories.OwnerStoreRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class OwnerBlueprintDtoProcessor implements DtoProcessor<BlueprintDto, Long> {
    private final RedeemRuleDtoProcessor redeemRuleDtoProcessor;
    private final OwnerBlueprintRepository blueprintRepository;
    private final OwnerStoreRepository storeRepository;

    public OwnerBlueprintDtoProcessor(@Lazy RedeemRuleDtoProcessor redeemRuleDtoProcessor, OwnerBlueprintRepository blueprintRepository, OwnerStoreRepository storeRepository) {
        this.redeemRuleDtoProcessor = redeemRuleDtoProcessor;
        this.blueprintRepository = blueprintRepository;
        this.storeRepository = storeRepository;
    }

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

        // redeemRuleDtos: Set empty
        /*if (dto.getRedeemRuleDtos() != null) {
            final Set<RedeemRuleDto> preprocessedRedeemRules = dto.getRedeemRuleDtos().stream()
                    .map(redeemRuleDtoProcessor::preprocessForPost).collect(Collectors.toSet());
            dto.setRedeemRuleDtos(preprocessedRedeemRules);
        }*/
        dto.setRedeemRuleDtos(new HashSet<>());

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }

    @Override
    public BlueprintDto preprocessForPut(Long id, BlueprintDto dto) {
        final Blueprint originalBlueprint = blueprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, id));
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
        if (dto.getNumMaxStamps() < originalBlueprint.getNumMaxStamps()) {
            violationMessages.add(
                    "BlueprintDto.numMaxStamp(" + dto.getNumMaxStamps() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxStamp(" + originalBlueprint.getNumMaxStamps() + ")");
        }
        if (dto.getNumMaxRedeems() < originalBlueprint.getNumMaxRedeems()) {
            violationMessages.add(
                    "BlueprintDto.numMaxRedeems(" + dto.getNumMaxRedeems() + ")" +
                            " must be larger than" +
                            " current blueprint.numMaxRedeems(" + originalBlueprint.getNumMaxRedeems() + ")");
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

        // storeId cannot be modified
        if (!Objects.equals(originalBlueprint.getStore().getId(), dto.getStoreId())) {
            violationMessages.add("Store cannot be changed");
        }

        // redeemRuleDtos: Set empty
        dto.setRedeemRuleDtos(new HashSet<>());

        if (!violationMessages.isEmpty()) {
            throw new DtoProcessingException("Failed to preprocess BlueprintDto", violationMessages);
        }

        return dto;
    }
}
