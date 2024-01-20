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
    private String numMaxStamps;
    private String numMaxRedeems;
    private String numMaxIssues;
    private OffsetDateTime expirationDate;
    private String bgImageId;
    private Boolean isPublishing;
    @JsonProperty("redeemRules")
    private Set<RedeemRuleDto> redeemRuleDtos;
    //private StoreDto store;
    private Long storeId;
}
