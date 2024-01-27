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

import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "card", indexes = @Index(columnList = "customerId"))
public class Card extends BaseEntity {
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