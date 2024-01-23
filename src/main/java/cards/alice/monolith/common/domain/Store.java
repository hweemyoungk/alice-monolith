package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
public class Store extends BaseEntity {
    private String description;
    private String zipcode;
    private String address;
    private String phone;
    @Column(precision = 10, scale = 7)
    private BigDecimal lat;
    @Column(precision = 10, scale = 7)
    private BigDecimal lng;
    private String bgImageId;
    private String profileImageId;
    @OneToMany(mappedBy = "store", cascade = CascadeType.PERSIST)
    private Set<Blueprint> blueprints;
    private UUID ownerId;
}
