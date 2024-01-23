package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BlueprintDto extends BaseDto {
    private String description;
    private String stampGrantCondDescription;
    private Integer numMaxStamps;
    private Integer numMaxRedeems;
    private Integer numMaxIssues;
    private OffsetDateTime expirationDate;
    private String bgImageId;
    private Boolean isPublishing;
    private Long storeId;
    @JsonProperty("redeemRules")
    private Set<RedeemRuleDto> redeemRuleDtos;
}
