package cards.alice.monolith.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "redeem")
public class Redeem extends BaseEntity {
    private Integer numStampsBefore;
    private Integer numStampsAfter;
    @ManyToOne
    @JoinColumn(name = "redeem_rule_id")
    private RedeemRule redeemRule;
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    private UUID token;
}