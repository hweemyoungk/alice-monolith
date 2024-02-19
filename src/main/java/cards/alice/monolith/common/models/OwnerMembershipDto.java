package cards.alice.monolith.common.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

// Just general detail of membership. Not allocated per user.
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OwnerMembershipDto extends MembershipDto {
    // -1 means infinite
    @Min(-1)
    private Integer numMaxAccumulatedTotalStores;
    @Min(-1)
    private Integer numMaxCurrentTotalStores;
    @Min(-1)
    private Integer numMaxCurrentActiveStores;
    @Min(-1)
    private Integer numMaxCurrentTotalBlueprintsPerStore;
    @Min(-1)
    private Integer numMaxCurrentActiveBlueprintsPerStore;
    @Min(-1)
    private Integer numMaxCurrentTotalRedeemRulesPerBlueprint;
    @Min(-1)
    private Integer numMaxCurrentActiveRedeemRulesPerBlueprint;

    public static List<OwnerMembershipDto> getCurrentOwnerMemberships(Map<String, OwnerMembershipDto> ownerMembershipMap) {
        final var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream()
                // Filter starts with "ROLE_owner-"
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_owner-"))
                // to "owner-..."
                .map(grantedAuthority -> grantedAuthority.getAuthority().replaceFirst("ROLE_", ""))
                // to OwnerMembershipDto
                .map(ownerMembershipMap::get)
                // Non-null only
                // TODO: Is it OK to ignore roles that don't exist in membershipMap?
                .filter(Objects::nonNull)
                .toList();
    }
}
