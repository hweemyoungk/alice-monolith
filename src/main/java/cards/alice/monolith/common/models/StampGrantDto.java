package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * DTO for {@link cards.alice.monolith.common.domain.StampGrant}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StampGrantDto extends BaseDto implements Serializable {
    @NotNull
    @Positive
    private Long cardId;
    @NotNull
    @Positive
    private Integer numStamps;
}