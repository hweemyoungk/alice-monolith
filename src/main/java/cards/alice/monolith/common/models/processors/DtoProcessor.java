package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.BaseDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

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
    T preprocessForPost(@Valid T dto);

    T preprocessForPut(ID id, @Valid T dto);
}
