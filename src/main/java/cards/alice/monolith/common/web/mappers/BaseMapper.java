package cards.alice.monolith.common.web.mappers;

public interface BaseMapper<E, D> {
    E toEntity(D dto);
    D toDto(E entity);
}
