package cards.alice.monolith.common.models;

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
public class RedeemRuleDto extends BaseDto<Long> {
    private String description;
    private Integer consumes;
    private String imageId;
    /**
     * Could be null when bound to unregistered blueprintDto
     */
    @Nullable
    private Long blueprintId;
}
