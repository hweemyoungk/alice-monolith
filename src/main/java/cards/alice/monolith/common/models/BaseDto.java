package cards.alice.monolith.common.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseDto<ID> implements Serializable {
    private ID id;
    private Integer version;
    @NotBlank
    @Length(max = 30)
    private String displayName;
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime createdDate;
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    private OffsetDateTime lastModifiedDate;
    @NotNull
    private Boolean isDeleted;

    public void setIdVersionNull() {
        id = null;
        version = null;
    }

    public void preprocessBaseForNew() {
        setIdVersionNull();
        isDeleted = false;
    }
}
