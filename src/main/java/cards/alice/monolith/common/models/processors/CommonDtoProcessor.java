package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.BaseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

@Validated
public abstract class CommonDtoProcessor<T extends BaseDto<ID>, ID> implements DtoProcessor<T, ID> {
    @Override
    public Collection<T> preprocessForPost(@NotEmpty @Valid Collection<T> dtos) {
        checkMembershipForPost(dtos);
        return dtos.stream().map(this::preprocessForPost).toList();
    }

    @Override
    public T preprocessForPostSingle(@Valid T dto) {
        checkMembershipForPost(List.of(dto));
        return preprocessForPost(dto);
    }

    /**
     * Usually, every examined entity must be EXCLUSIVELY LOCKED throughout the whole transaction.
     * @param dtos Dtos to post
     */
    protected abstract void checkMembershipForPost(Collection<T> dtos);

    protected abstract T preprocessForPost(T dto);
}
