package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class StampGrantDto extends LongDto implements Serializable {
    @NotNull
    @Positive
    private Integer numStamps;
    @JsonProperty("card")
    private CardDto cardDto;
    @NotNull
    @Positive
    private Long cardId;
}