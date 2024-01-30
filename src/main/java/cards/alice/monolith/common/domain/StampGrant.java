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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@GenericGenerator(
        name = "long-generator",
        type = SequenceStyleGenerator.class,
        parameters = {
                @Parameter(name = "sequence_name", value = "stamp-grant-id-sequence"),
                @Parameter(name = "initial_value", value = "11"),
                @Parameter(name = "increment_size", value = "1")
        }
)
@Table(name = "stamp_grant")
public class StampGrant extends LongEntity {
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