package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    private Long ttlSeconds;
    @NotNull
    @Positive
    private Long cardId;
    @NotNull
    @Positive
    private Long redeemRuleId;
    @NotNull
    @NotBlank
    private String customerDisplayName;
    @NotNull
    private UUID customerId;

    private UUID ownerId;
    private String blueprintDisplayName;
    @Positive
    private Long expMilliseconds;
    private Boolean isRedeemed;

    public void setExpMillisecondsFromNow(long milliseconds) {
        expMilliseconds = Instant.now().toEpochMilli() + milliseconds;
    }
}