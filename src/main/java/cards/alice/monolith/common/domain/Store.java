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
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

//@NamedEntityGraph(
//        name = "owner-models",
//        attributeNodes = {
//                @NamedAttributeNode(value = "blueprints", subgraph = "blueprint-subgraph")
//        },
//        subgraphs = {
//                @NamedSubgraph(
//                        name = "blueprint-subgraph",
//                        attributeNodes = {
//                                @NamedAttributeNode("redeemRules")
//                        }
//                )
//        }
//)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "store-id-sequence",
        initialValue = 1,
        allocationSize = 50
)
@Table(name = "store", indexes = @Index(columnList = "ownerId"))
public class Store extends LongEntity {
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
    @Column(precision = 10, scale = 7)
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal lat;
    @Column(precision = 10, scale = 7)
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal lng;

    @NotNull
    private UUID ownerId;
    private String bgImageId;
    private String profileImageId;

    @OneToMany(mappedBy = "store")
    private Set<Blueprint> blueprints;

    @PreRemove
    private void cascadeSetNull() {
        Set<Blueprint> blueprints1 = getBlueprints();
        if (blueprints1 != null) {
            blueprints1.forEach(blueprint -> blueprint.setStore(null));
        }
    }
}
