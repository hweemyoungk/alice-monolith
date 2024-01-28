package cards.alice.monolith.common.models.processors;

import cards.alice.monolith.common.models.BaseDto;

/**
 * Use-case-specific processor for Dto.<br>
 * Every 'process' can include
 * <ul>Overwriting properties(weak validation)</ul>
 * <ul>Throwing exceptions(strict validation)</ul>
 * <ul>Authentication</ul>
 *
 * @param <T>  DTO type
 * @param <ID> ID type of DTO
 */
public interface DtoProcessor<T extends BaseDto, ID> {
    T preprocessForPost(T dto);

    T preprocessForPut(ID id, T dto);
}
