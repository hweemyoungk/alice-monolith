package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "card-id-sequence",
        initialValue = 1,
        allocationSize = 50
)
@Table(name = "card", indexes = @Index(columnList = "customerId"))
public class Card extends LongEntity {
    @NotNull
    @PositiveOrZero
    private Integer numCollectedStamps;
    @NotNull
    @Positive
    private Integer numGoalStamps;
    @NotNull
    private Boolean isFavorite;
    @NotNull
    @PositiveOrZero
    private Integer numRedeemed;
    @NotNull
    private Boolean isDiscarded;
    @NotNull
    private Boolean isUsedOut;
    @NotNull
    private Boolean isInactive;
    private String bgImageId;
    @NotNull
    private UUID customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
    @OneToMany(mappedBy = "card")
    private Set<StampGrant> stampGrants;
    @OneToMany(mappedBy = "card")
    private Set<Redeem> redeems;

    @PreRemove
    private void cascadeSetNull() {
        Set<StampGrant> stampGrants1 = getStampGrants();
        if (stampGrants1 != null) {
            stampGrants1.forEach(stampGrant -> stampGrant.setCard(null));
        }
        Set<Redeem> redeems1 = getRedeems();
        if (redeems1 != null) {
            redeems1.forEach(redeem -> redeem.setCard(null));
        }
    }
}