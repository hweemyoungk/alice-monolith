package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BlueprintDto extends LongDto implements Serializable {
    //private static final Long serialVersionUID = 1L;

    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotBlank
    @Length(max = 1000)
    private String stampGrantCondDescription;
    @NotNull
    @Positive
    @Max(100)
    private Integer numMaxStamps;
    @NotNull
    @Positive
    @Max(100)
    private Integer numMaxRedeems;
    @NotNull
    @Positive
    @Max(100)
    private Integer numMaxIssuesPerCustomer;
    @NotNull
    @PositiveOrZero
    private Integer numMaxIssues;
    @NotNull
    private OffsetDateTime expirationDate;
    private String bgImageId;
    @NotNull
    private Boolean isPublishing;
    @JsonProperty("store")
    private StoreDto storeDto;
    @NotNull
    @Positive
    private Long storeId;
    @JsonProperty("redeemRules")
    private Set<RedeemRuleDto> redeemRuleDtos;
}
