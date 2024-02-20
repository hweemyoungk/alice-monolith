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
public class CustomerMembershipDto extends MembershipDto {
    // -1 means infinite
    @Min(-1)
    private Integer numMaxAccumulatedTotalCards;
    @Min(-1)
    private Integer numMaxCurrentTotalCards;
    @Min(-1)
    private Integer numMaxCurrentActiveCards;

    public static List<CustomerMembershipDto> getCurrentCustomerMemberships(Map<String, CustomerMembershipDto> customerMembershipMap) {
        final var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return authorities.stream()
                // Filter starts with "ROLE_customer-"
                .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_customer-"))
                // to "customer-..."
                .map(grantedAuthority -> grantedAuthority.getAuthority().replaceFirst("ROLE_", ""))
                // to CustomerMembershipDto
                .map(customerMembershipMap::get)
                // Non-null only
                // TODO: Is it OK to ignore roles that don't exist in membershipMap?
                .filter(Objects::nonNull)
                .toList();
    }
}
