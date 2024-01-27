package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.hibernate.validator.constraints.Length;

import java.time.OffsetDateTime;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;
    @Version
    private Integer version;
    @NotBlank
    @Length(max = 30)
    private String displayName;
    @Column(updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @CreationTimestamp
    private OffsetDateTime createdDate;
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @UpdateTimestamp
    private OffsetDateTime lastModifiedDate;
    @NotNull
    @Builder.Default
    private Boolean isDeleted = Boolean.FALSE;
}
