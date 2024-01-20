package cards.alice.monolith.common.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseDto implements Serializable {
    private Long id;
    private Integer version;
    @NotBlank
    @Max(30)
    private String displayName;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;
    private Boolean isDeleted;
}
