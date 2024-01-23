package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
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
    private Integer numMaxStamps;
    private Integer numMaxRedeems;
    private Integer numMaxIssues;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private OffsetDateTime expirationDate;
    private String bgImageId;
    private Boolean isPublishing;
    @OneToMany(
            mappedBy = "blueprint", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private Set<RedeemRule> redeemRules;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    // @Column(name = "store_id", insertable = false, updatable = false)
    // private Long storeId;
}