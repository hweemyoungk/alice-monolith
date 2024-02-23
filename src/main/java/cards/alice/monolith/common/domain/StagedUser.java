package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "staged-user-id-sequence",
        initialValue = 1,
        allocationSize = 10
)
@Table(
        name = "staged_user",
        indexes = @Index(columnList = "userId", unique = true))
public class StagedUser extends LongEntity {
    @NotNull
    private UUID userId;

    @OneToMany
    @JoinColumn(
            name = "customerId",
            referencedColumnName = "userId")
    private Set<Card> cards;

    @OneToMany
    @JoinColumn(
            name = "ownerId",
            referencedColumnName = "userId")
    private Set<Store> stores;

    @PreRemove
    private void cascadeSetNull() {
        getCards().forEach(card -> {
            card.setCustomerId(null);
        });
        getStores().forEach(store -> {
            store.setOwnerId(null);
        });
    }
}