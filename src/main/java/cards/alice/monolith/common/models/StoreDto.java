package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StoreDto extends BaseDto {
    @Max(1000)
    @NotBlank
    private String description;
    @NotBlank
    @Max(7)
    private String zipcode;
    @NotBlank
    @Max(120)
    private String address;
    @NotBlank
    @Max(15)
    private String phone;
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @NotNull
    private BigDecimal lat;
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @NotNull
    private BigDecimal lng;
    private String bgImageId;
    private String profileImageId;
    @JsonProperty("blueprints")
    private Set<BlueprintDto> blueprintDtos;
    @NotNull
    private UUID ownerId;
}
