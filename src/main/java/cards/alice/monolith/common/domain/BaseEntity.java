package cards.alice.monolith.common.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
@SuperBuilder
public abstract class BaseEntity {
    /*@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Version
    private Integer version;

    private String displayName;

    @CreationTimestamp
    @Column(updatable = false)
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private OffsetDateTime createdDate;

    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    @UpdateTimestamp
    private OffsetDateTime lastModifiedDate;

    @Builder.Default
    private Boolean isDeleted = false;
}
