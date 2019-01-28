package nl.freelist.domain.interfaces;

import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.events.Event;

public interface Repository<T> {

  ResultObject<T> insert(T item);

  void insert(Iterable<T> items);

  void update(T item);

  void delete(T item);

  void delete(Specifiable specification);

  List<T> query(Specifiable specification);

  T getById(String uuid);

  List<Event> getSavedEventsFor(String uuid);
}