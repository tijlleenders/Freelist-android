package nl.freelist.repository;

import java.util.List;

public interface Repository<T> {

  void insert(T item);

  void insert(Iterable<T> items);

  void update(T item);

  void delete(T item);

  void delete(Specification specification);

  List<T> query(Specification specification);
}