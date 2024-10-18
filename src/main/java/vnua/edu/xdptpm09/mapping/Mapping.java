package vnua.edu.xdptpm09.mapping;

import java.util.List;

public interface Mapping<E, D> {
    E toEntity(D dto);

    D toDto(E entity);

    E updateFromDTO(D dto);

    List<E> toEntity(List<D> dto);

    List<D> toDto(List<E> entity);
}
