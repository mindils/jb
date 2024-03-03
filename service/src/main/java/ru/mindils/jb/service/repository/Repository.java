package ru.mindils.jb.service.repository;


import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import ru.mindils.jb.service.entity.BaseEntity;

public interface Repository<K extends Serializable, E extends BaseEntity<K>> {

  E save(E entity);

  void delete(E entity);

  void update(E entity);

  default Optional<E> findById(K id) {
    return findById(id, Map.of());
  }

  Optional<E> findById(K id, Map<String, Object> properties);

}
