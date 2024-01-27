package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BlueprintDto extends BaseDto {
    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotBlank
    @Length(max = 100)
    private String stampGrantCondDescription;
    @NotNull
    @PositiveOrZero
    private Integer numMaxStamps;
    @NotNull
    @Positive
    private Integer numMaxRedeems;
    @NotNull
    @Positive
    private Integer numMaxIssuesPerCustomer;
    @NotNull
    @PositiveOrZero
    private Integer numMaxIssues;
    @NotNull
    private OffsetDateTime expirationDate;
    private String bgImageId;
    @NotNull
    private Boolean isPublishing;
    @NotNull
    @Positive
    private Long storeId;
    @JsonProperty("redeemRules")
    private Set<RedeemRuleDto> redeemRuleDtos;
}
