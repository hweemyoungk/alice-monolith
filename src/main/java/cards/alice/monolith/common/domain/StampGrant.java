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

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "stamp-grant-id-sequence",
        initialValue = 1,
        allocationSize = 50
)
@Table(name = "stamp_grant")
public class StampGrant extends LongEntity {
    @NotNull
    @Positive
    private Integer numStamps;
    @NotNull
    @PositiveOrZero
    private Integer numStampsBefore;
    @NotNull
    @Positive
    private Integer numStampsAfter;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;
}