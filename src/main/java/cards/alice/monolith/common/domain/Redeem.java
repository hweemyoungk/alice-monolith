package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

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
@Table(name = "redeem")
public class Redeem extends LongEntity {
    @NotNull
    @PositiveOrZero
    private Integer numStampsBefore;
    @NotNull
    @PositiveOrZero
    private Integer numStampsAfter;
    @ManyToOne
    @JoinColumn(name = "redeem_rule_id")
    private RedeemRule redeemRule;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    @NotNull
    private UUID token;
}