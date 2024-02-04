package cards.alice.monolith.common.web.mappers;

import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUtil;

public interface BaseMapper<E, D> {
    PersistenceUtil PERSISTENCE_UTIL = Persistence.getPersistenceUtil();

    /**
     * Output <code>entity</code> shall be persisted without any further modification.<br>
     * Input <code>dto</code> should have <b>completed every necessary preprocess</b>.<br>
     * Associated entity with <i>many-to-one</i> relation shall be a reference by <code>dto.manyToOneId</code>
     * and <code>dto.manyToOneDto</code> will be <b>ignored</b>.<br>
     * Associated entities with <i>one-to-many</i> relation shall be raw output from target mapper.
     * @param dto Dto to convert
     * @return Produced entity
     */
    E toEntity(D dto);

    /**
     * Input <code>entity</code> shall not be modified since loaded from persistence layer.<br>
     * Associated entities that have not been loaded shall be converted to <b>null</b>.<br>
     * Still, associated entity with <i>many-to-one</i> relation shall be <b>initialized eventually</b> and assign its id to <code>dto.manyToOneId</code>.
     * @param entity Entity to convert
     * @return Produced dto
     */
    D toDto(E entity);

    /**
     * Patch <code>entity</code> with <code>dto</code> and return <code>entity</code>.<br>
     * Output <code>entity</code> shall be persisted without any further modification.<br>
     * Associated entity with <i>many-to-one</i> relation shall be a reference by <code>dto.manyToOneId</code>
     * and <code>dto.manyToOneDto</code> will be <b>ignored</b>.<br>
     * Associated entities with <i>one-to-many</i> relation shall be raw output from target mapper.
     * @param dto
     * @param entity
     * @return Patched entity
     */
    E partialUpdate(D dto, E entity);
}
