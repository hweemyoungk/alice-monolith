package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * DTO for {@link cards.alice.monolith.common.domain.Redeem}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemDto extends BaseDto {
    @NotNull
    @Positive
    private Integer numStampsBefore;
    @NotNull
    @PositiveOrZero
    private Integer numStampsAfter;
    @NotNull
    @Positive
    private Long redeemRuleId;
    @NotNull
    @Positive
    private Long cardId;
    private UUID token;
}