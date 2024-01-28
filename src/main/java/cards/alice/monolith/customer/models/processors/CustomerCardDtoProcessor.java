package cards.alice.monolith.customer.models.processors;

import cards.alice.monolith.common.domain.Blueprint;
import cards.alice.monolith.common.models.CardDto;
import cards.alice.monolith.common.models.processors.DtoProcessor;
import cards.alice.monolith.common.web.exceptions.ResourceNotFoundException;
import cards.alice.monolith.customer.repositories.CustomerBlueprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerCardDtoProcessor implements DtoProcessor<CardDto, Long> {
    private final CustomerBlueprintRepository blueprintRepository;

    @Override
    public CardDto preprocessForPost(CardDto dto) {
        // id, version, isDeleted
        dto.preprocessBaseForNew();

        // numCollectedStamps must be 0
        dto.setNumCollectedStamps(0);

        // numRedeemed must be 0
        dto.setNumRedeemed(0);

        // isDiscarded, isUsedOut, isInactive must be false
        dto.setIsDiscarded(false);
        dto.setIsUsedOut(false);
        dto.setIsInactive(false);

        // customerId must exist
        // Validated by @PreAuthorize postCard

        // blueprintId must exist
        Blueprint blueprint = blueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));

        // numGoalStamps must be less or equal to blueprint.numMaxStamps
        dto.setNumGoalStamps(Math
                .min(dto.getNumGoalStamps(), blueprint.getNumMaxStamps()));

        // (If strict) expirationDate must be equal to blueprint.expirationDate
        dto.setExpirationDate(null);

        // (If strict) storeId must equal to blueprint.store.id
        dto.setStoreId(null);

        return dto;
    }

    @Override
    public CardDto preprocessForPut(Long id, CardDto dto) {
        // id, version must be null (will be provided by entity)
        dto.setIdVersionNull();

        // isDeleted
        // Validated by @Validated CardDto

        // Customer cannot control numCollectedStamps
        dto.setNumCollectedStamps(null);

        // Customer cannot control numRedeemed
        // (Owner can during approveRedeemRequest)
        dto.setNumRedeemed(null);

        // Customer can set isDiscarded only to true
        if (!dto.getIsDiscarded()) dto.setIsDiscarded(null);

        // Customer cannot control isUsedOut, isInactive
        dto.setIsUsedOut(null);
        dto.setIsInactive(null);

        // customerId must exist
        // Validated by @PreAuthorize putCard

        // blueprintId must exist
        Blueprint blueprint = blueprintRepository.findById(dto.getBlueprintId())
                .orElseThrow(() -> new ResourceNotFoundException(Blueprint.class, dto.getBlueprintId()));

        // numGoalStamps must be less or equal to blueprint.numMaxStamps
        dto.setNumGoalStamps(Math
                .min(dto.getNumGoalStamps(), blueprint.getNumMaxStamps()));

        // (If strict) expirationDate must be equal to blueprint.expirationDate
        dto.setExpirationDate(null);

        // (If strict) storeId must equal to blueprint.store.id
        dto.setStoreId(null);

        return dto;
    }
}
