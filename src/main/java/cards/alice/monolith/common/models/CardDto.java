package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
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
public class CardDto extends LongDto implements Serializable {
    //private static final Long serialVersionUID = 1L;

    @NotNull
    private Boolean isDiscarded;
    @NotNull
    private Boolean isUsedOut;
    @NotNull
    private Boolean isInactive;
    @NotNull
    @PositiveOrZero
    private Integer numCollectedStamps;
    @NotNull
    @Positive
    private Integer numGoalStamps;
    /**
     * Retrieves value from {@link cards.alice.monolith.common.domain.Blueprint Card.blueprint}.
     */
    private OffsetDateTime expirationDate;
    @NotNull
    private Boolean isFavorite;
    @NotNull
    @PositiveOrZero
    private Integer numRedeemed;
    private String bgImageId;
    @NotNull
    private UUID customerId;
    @JsonProperty("blueprint")
    private BlueprintDto blueprintDto;
    @NotNull
    @Positive
    private Long blueprintId;
}