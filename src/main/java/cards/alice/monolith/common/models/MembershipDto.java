package cards.alice.monolith.common.models;

import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.access.AuthorizationServiceException;

import java.util.Collection;

// Just general detail of membership. Not allocated per user.
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class MembershipDto extends LongDto {
    // Monolith service picks the highest membership among candidates.
    // Must be unique across the same class
    @Positive
    @Builder.Default
    private Integer priority = 1;

    public static MembershipDto highestPriority(Collection<? extends MembershipDto> membershipDtos) {
        return membershipDtos.stream().reduce((prev, cur) ->
                        prev.getPriority() < cur.getPriority() ? cur : prev)
                .orElseThrow(() -> new AuthorizationServiceException("No membership provided"));
    }
}
