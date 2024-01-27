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
import org.hibernate.validator.constraints.Length;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "redeem_rule")
public class RedeemRule extends BaseEntity {
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