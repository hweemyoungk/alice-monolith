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

/**
 * DTO for {@link cards.alice.monolith.common.domain.Redeem}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemDto extends LongDto implements Serializable {
    //private static final Long serialVersionUID = 1L;

    @NotNull
    private String redeemRequestId;
    @NotNull
    @Positive
    private Integer numStampsBefore;
    @NotNull
    @PositiveOrZero
    private Integer numStampsAfter;
    @JsonProperty("redeemRule")
    private RedeemRuleDto redeemRuleDto;
    @NotNull
    @Positive
    private Long redeemRuleId;
    @JsonProperty("card")
    private CardDto cardDto;
    @NotNull
    @Positive
    private Long cardId;
}