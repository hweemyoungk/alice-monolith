package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "redeem-rule-id-sequence",
        initialValue = 1,
        allocationSize = 50
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
    @OneToMany(mappedBy = "redeemRule")
    private Set<Redeem> redeems;

    @PreRemove
    private void cascadeSetNull() {
        Set<Redeem> redeems1 = getRedeems();
        if (redeems1 != null) {
            redeems1.forEach(redeem -> redeem.setRedeemRule(null));
        }
    }
}