package cards.alice.monolith.common.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

// TODO: Reconstruct with Spring Redis
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemRequestDto {
    private static final String OWNER_REDEEM_REQUESTS_KEY_PREFIX = "redeemRequests:";
    private static final Pattern OWNER_REDEEM_REQUESTS_KEY_PREFIX_PATTERN = Pattern.compile("(?<=" + OWNER_REDEEM_REQUESTS_KEY_PREFIX + ").+");

    private String id;
    @NotNull
    private UUID customerId;
    @NotNull
    @NotBlank
    private String customerDisplayName;
    @Value("${cards.alice.customer.owner-id}")
    @NotNull
    @Positive
    private Long cardId;
    @NotNull
    @Positive
    private Long redeemRuleId;

    private UUID ownerId;
    private UUID token;
    private String blueprintDisplayName;
    @Positive
    private Long expMilliseconds;
    private Boolean isRedeemed = false;

    public static String getOwnerRedeemRequestsKey(String ownerId) {
        return OWNER_REDEEM_REQUESTS_KEY_PREFIX + ownerId;
    }

    public String getOwnerRedeemRequestsKey() {
        return getOwnerRedeemRequestsKey(ownerId.toString());
    }


    public String getFieldName() {
        return String.join(":", redeemRuleId.toString(), cardId.toString());
    }

    public String getId() {
        // redeemRequests:{ownerId}:{redeemRuleId}:{cardId}#{token}
        return OWNER_REDEEM_REQUESTS_KEY_PREFIX
                + String.join(":", ownerId.toString(), redeemRuleId.toString(), cardId.toString())
                + "#" + token.toString();
    }

    public void setTtlMillisecondsFromNow(long milliseconds) {
        expMilliseconds = Instant.now().toEpochMilli() + milliseconds;
    }

    public RedeemRequestDto(String id) {
        final String[] split = id.split("#");
        token = UUID.fromString(split[1]);
        String keyWithFieldName = split[0].replaceFirst(OWNER_REDEEM_REQUESTS_KEY_PREFIX, "");
        String[] split1 = keyWithFieldName.split(":");
        ownerId = UUID.fromString(split1[0]);
        redeemRuleId = Long.parseLong(split1[1]);
        cardId = Long.parseLong(split1[2]);
    }
}
