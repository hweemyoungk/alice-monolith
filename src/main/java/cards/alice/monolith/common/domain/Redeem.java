package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "redeem-id-sequence",
        initialValue = 1,
        allocationSize = 50
)
@Table(
        name = "redeem",
        indexes = {@Index(columnList = "redeemRequestId")})
public class Redeem extends LongEntity {
    @NotNull
    private String redeemRequestId;
    @NotNull
    @PositiveOrZero
    private Integer numStampsBefore;
    @NotNull
    @PositiveOrZero
    private Integer numStampsAfter;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redeem_rule_id")
    private RedeemRule redeemRule;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;
}