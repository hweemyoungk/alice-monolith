package cards.alice.monolith.common.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;
    private Boolean isDeleted;

    public void setIdVersionNull() {
        id = null;
        version = null;
    }
}
