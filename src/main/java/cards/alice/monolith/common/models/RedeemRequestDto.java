package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

// TODO: Determine structure
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemRequestDto {
    private String id;
    @NotNull
    @Positive
    private Long cardId;
    @NotNull
    @Positive
    private Long redeemRuleId;
}
