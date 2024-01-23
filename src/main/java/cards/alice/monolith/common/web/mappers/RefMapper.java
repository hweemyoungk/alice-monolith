package cards.alice.monolith.common.web.mappers;

import cards.alice.monolith.common.domain.BaseEntity;
import cards.alice.monolith.common.models.BaseDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefMapper <E extends BaseEntity, D extends BaseDto> {
    private final Class<E> entityClass;
    private final BaseMapper<E, D> baseMapper;
    private final EntityManager entityManager;
    public D toDto(E entity) {
        return baseMapper.toDto(entity);
    }

    public E toEntity(D dto) {
        return entityManager.getReference(entityClass, dto.getId());
    }
}
