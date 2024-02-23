package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;
import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
        name = "long-generator",
        sequenceName = "blueprint-id-sequence",
        initialValue = 1,
        allocationSize = 50
)
@Table(name = "blueprint")
public class Blueprint extends LongEntity {
    @NotBlank
    @Length(max = 1000)
    private String description;
    @NotBlank
    @Length(max = 100)
    private String stampGrantCondDescription;
    @NotNull
    @Positive
    private Integer numMaxStamps;
    @NotNull
    @PositiveOrZero
    private Integer numMaxRedeems;
    @NotNull
    @Positive
    private Integer numMaxIssuesPerCustomer;
    @NotNull
    @PositiveOrZero
    private Integer numMaxIssues;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @NotNull
    private OffsetDateTime expirationDate;
    private String bgImageId;
    @NotNull
    private Boolean isPublishing;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @OneToMany(mappedBy = "blueprint")
    private Set<RedeemRule> redeemRules;
    @OneToMany(mappedBy = "blueprint")
    private Set<Card> cards;

    @PreRemove
    private void cascadeSetNull() {
        getRedeemRules().forEach(redeemRule -> redeemRule.setBlueprint(null));
        getCards().forEach(card -> card.setBlueprint(null));
    }
}