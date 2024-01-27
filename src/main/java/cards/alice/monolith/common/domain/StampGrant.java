package cards.alice.monolith.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Table(name = "stamp_grant")
public class StampGrant extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;
    @NotNull
    @Positive
    private Integer numStamps;
    @NotNull
    @PositiveOrZero
    private Integer numStampsBefore;
    @NotNull
    @Positive
    private Integer numStampsAfter;
}