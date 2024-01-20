package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "card", indexes = @Index(columnList = "customerId"))
public class Card extends BaseEntity {
    private Integer numCollectedStamps;
    private Integer numGoalStamps;
    //private Integer numMaxStamps; // From blueprint
    private Timestamp expirationDate;
    private Boolean isFavorite;
    //private Integer numMaxRedeems; // From blueprint
    private Integer numRedeemed;
    private UUID customerId;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @Column(name = "store_id", insertable = false, updatable = false)
    private Long storeId;
    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
    @Column(name = "blueprint_id", insertable = false, updatable = false)
    private Long blueprintId;
    private String bgImageId;
    private Boolean isDiscarded;
    private Boolean isUsedOut;
    private Boolean isInactive;
}