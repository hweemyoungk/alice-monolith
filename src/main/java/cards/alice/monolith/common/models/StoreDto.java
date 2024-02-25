package cards.alice.monolith.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StoreDto extends LongDto implements Serializable {
    //private static final Long serialVersionUID = 1L;

    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotNull
    private Boolean isClosed;
    @NotNull
    private Boolean isInactive;

    @Length(max = 7)
    private String zipcode;
    @Length(max = 120)
    private String address;
    @Length(max = 15)
    private String phone;
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal lat;
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal lng;

    @NotNull
    private UUID ownerId;
    private String bgImageId;
    private String profileImageId;

    @JsonProperty("blueprints")
    private Set<BlueprintDto> blueprintDtos;
}
