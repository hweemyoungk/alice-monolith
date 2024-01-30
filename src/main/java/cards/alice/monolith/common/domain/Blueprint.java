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
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
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
@GenericGenerator(
        name = "long-generator",
        type = SequenceStyleGenerator.class,
        parameters = {
                @Parameter(name = "sequence_name", value = "blueprint-id-sequence"),
                @Parameter(name = "initial_value", value = "11"),
                @Parameter(name = "increment_size", value = "1")
        }
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
    @OneToMany(
            mappedBy = "blueprint", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private Set<RedeemRule> redeemRules;
    /*@OneToMany(
            mappedBy = "blueprint", cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    private Set<Card> cards;*/
    @ManyToOne(optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}