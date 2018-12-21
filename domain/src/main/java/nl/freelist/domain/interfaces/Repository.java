package nl.freelist.domain.interfaces;

import java.util.List;

public interface Repository<T> {

  void insert(T item);

  void insert(Iterable<T> items);

  void update(T item);

  void delete(T item);

  void delete(Specifiable specification);

  List<T> query(Specifiable specification);

  T getById(int id);

  List<T> getAllEntries();
}