package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.BaseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

/**
 * Use-case-specific processor for Dto.<br>
 * Every 'process' can include
 * <ul>Overwriting properties(weak validation)</ul>
 * <ul>Throwing ConstraintViolationExceptions by @Valid(strict validation)</ul>
 * <ul>Authentication</ul>
 *
 * @param <T>  DTO type
 * @param <ID> ID type of DTO
 */
@Validated
public interface DtoProcessor<T extends BaseDto, ID> {
    // @Valid Collection works? : YES!
    // Caused by: jakarta.validation.ConstraintViolationException: preprocessForPost.dtos[0].isDiscarded: must not be null
    Collection<T> preprocessForPost(@NotEmpty @Valid Collection<T> dtos);

    T preprocessForPostSingle(@Valid T dto);

    T preprocessForPut(ID id, @Valid T dto);
}
