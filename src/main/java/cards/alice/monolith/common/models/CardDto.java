package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link cards.alice.monolith.common.domain.Card}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CardDto extends BaseDto {
    @NotBlank
    private String displayName;
    @NotNull
    @PositiveOrZero
    private Integer numCollectedStamps;
    @NotNull
    @Positive
    private Integer numGoalStamps;
    /**
     * Retrieves value from {@link cards.alice.monolith.common.domain.Blueprint Card.blueprint}.
     *
     */
    @NotNull
    private OffsetDateTime expirationDate;
    @NotNull
    private Boolean isFavorite;
    @NotNull
    @PositiveOrZero
    private Integer numRedeemed;
    private String bgImageId;
    @NotNull
    private Boolean isDiscarded;
    @NotNull
    private Boolean isUsedOut;
    @NotNull
    private Boolean isInactive;
    @NotNull
    private UUID customerId;
    @NotNull
    private Long storeId;
    @NotNull
    private Long blueprintId;
    //@NotNull
    //@JsonProperty("blueprintId")
    //@JsonSerialize(using = BlueprintIdSerializer.class)
    //@JsonDeserialize(using = BlueprintIdDeserializer.class)
    //private BlueprintDto blueprintDto;
}