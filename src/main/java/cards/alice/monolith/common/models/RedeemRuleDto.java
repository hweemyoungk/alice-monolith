package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemRuleDto extends LongDto {
    @NotBlank
    @Length(max = 100)
    private String description;
    @NotNull
    @PositiveOrZero
    private Integer consumes;
    private String imageId;
    @JsonProperty("blueprint")
    private BlueprintDto blueprintDto;
    /**
     * Could be null when bound to unregistered blueprintDto
     */
    private Long blueprintId;
    @JsonProperty("redeems")
    private Set<RedeemDto> redeemDtos;
}
