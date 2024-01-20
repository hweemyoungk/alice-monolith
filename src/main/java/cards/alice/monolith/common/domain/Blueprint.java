package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "blueprint")
public class Blueprint extends BaseEntity {
    private String description;
    private String stampGrantCondDescription;
    private String numMaxStamps;
    private String numMaxRedeems;
    private String numMaxIssues;
    private Timestamp expirationDate;
    private String bgImageId;
    private Boolean isPublishing;
    @OneToMany(mappedBy = "blueprint", cascade = CascadeType.PERSIST)
    private Set<RedeemRule> redeemRules;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "store_id", insertable = false, updatable = false)
    private Long storeId;
}