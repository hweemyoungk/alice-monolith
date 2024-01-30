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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.util.UUID;

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
                @Parameter(name = "sequence_name", value = "card-id-sequence"),
                @Parameter(name = "initial_value", value = "11"),
                @Parameter(name = "increment_size", value = "1")
        }
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
    private UUID customerId;
    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
    private String bgImageId;
    @NotNull
    private Boolean isDiscarded;
    @NotNull
    private Boolean isUsedOut;
    @NotNull
    private Boolean isInactive;
}