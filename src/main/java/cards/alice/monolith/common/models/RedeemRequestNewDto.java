package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRequestNewDto extends StringDto implements Serializable {
    //private static final Long serialVersionUID = 1L;

    @NotNull
    @Positive
    private Long cardId;
    @JsonProperty("redeemRule")
    private RedeemRuleDto redeemRuleDto;
    @NotNull
    @Positive
    private Long redeemRuleId;
    @NotBlank
    @Length(max = 30)
    private String customerDisplayName;
    @NotNull
    private UUID customerId;

    @Positive
    private Long ttlSeconds;
    private UUID ownerId;
    private String blueprintDisplayName;
    @Positive
    private Long expMilliseconds;
    private Boolean isRedeemed;

    public void setExpMillisecondsFromNow(long milliseconds) {
        expMilliseconds = Instant.now().toEpochMilli() + milliseconds;
    }
}