package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "store", indexes = @Index(columnList = "ownerId"))
@GenericGenerator(
        name = "long-generator",
        type = SequenceStyleGenerator.class,
        parameters = {
                @Parameter(name = "sequence_name", value = "store-id-sequence"),
                @Parameter(name = "initial_value", value = "11"),
                @Parameter(name = "increment_size", value = "1")
        }
)
public class Store extends LongEntity {
    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotBlank
    @Length(max = 7)
    private String zipcode;
    @NotBlank
    @Length(max = 120)
    private String address;
    @NotBlank
    @Length(max = 15)
    private String phone;
    @Column(precision = 10, scale = 7)
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal lat;
    @Column(precision = 10, scale = 7)
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal lng;
    private String bgImageId;
    private String profileImageId;
    @NotNull
    private UUID ownerId;
    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private Set<Blueprint> blueprints;
}
