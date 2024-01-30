package cards.alice.monolith.common.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.Length;

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
                @Parameter(name = "sequence_name", value = "redeem-rule-id-sequence"),
                @Parameter(name = "initial_value", value = "11"),
                @Parameter(name = "increment_size", value = "1")
        }
)
@Table(name = "redeem_rule")
public class RedeemRule extends LongEntity {
    @NotBlank
    @Length(max = 100)
    private String description;
    @NotNull
    @PositiveOrZero
    private Integer consumes;
    private String imageId;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
}