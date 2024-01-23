package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "redeem_rule")
public class RedeemRule extends BaseEntity {
    private String description;
    private Integer consumes;
    private String imageId;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "blueprint_id")
    private Blueprint blueprint;
}