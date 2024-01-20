package cards.alice.monolith.common.models;

import cards.alice.monolith.common.domain.Blueprint;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RedeemRuleDto extends BaseDto {
    private String description;
    private Integer consumes;
    private String imageId;
    /**
     * Could be null when bound to unregistered blueprintDto
     */
    @Nullable
    private Long blueprintId;
}
