package nl.freelist.data;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.interfaces.Specifiable;

public class EntryRepository implements nl.freelist.domain.interfaces.Repository<Entry> {

  private static final String TAG = "EntryRepository";

  private EventDatabaseHelper eventDatabaseHelper;

  public EntryRepository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
  }

  @Override
  public ResultObject<Entry> insert(Entry entry) {

    List<Query> queryList = new ArrayList<>();

    int expectedEventSequenceNumberToSave =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(entry.getUuid().toString()) + 1;

    List<Event> newEventsToSave = entry.getEventList(expectedEventSequenceNumberToSave);
    for (Event event : newEventsToSave) {
      try {
        eventDatabaseHelper.addEvent("entry",
            expectedEventSequenceNumberToSave - 1 + newEventsToSave.indexOf(event), event, entry);
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Entry entry)");
      }
    }

    return new ResultObject<Entry>(true);
  }

  @Override
  public void insert(Iterable<Entry> items) {
  }

  @Override
  public void update(Entry item) {
  }

  @Override
  public void delete(Entry item) {
  }

  @Override
  public void delete(Specifiable specification) {
  }

  @Override
  public List<Entry> query(Specifiable specification) {
    List<Entry> allEntries = new ArrayList<>();
    return allEntries;
  }

  @Override
  public Entry getById(String uuid) {
    EntryCreatedEvent entryCreatedEvent = eventDatabaseHelper.getEntryCreatedEvent(uuid);
    Entry entry = new Entry(UUID.fromString(entryCreatedEvent.getOwnerUuid()),
        UUID.fromString(entryCreatedEvent.getParentUuid()),
        UUID.fromString(entryCreatedEvent.getEntryId()), "", "", 0);
    return entry;
  }

  @Override
  public List<Event> getSavedEventsFor(String entryId) {
    List<Event> eventList = eventDatabaseHelper.getEventsFor(entryId);
    return eventList;
  }

  public ViewModelEntry getViewModelEntryById(UUID uuid) {
    return eventDatabaseHelper.viewModelEntryFor(uuid.toString());
  }

  public List<ViewModelEntry> getBreadcrumbViewModelEntries(UUID parentUuid) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();

    if (!parentUuid.equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()))) {
      ViewModelEntry parentViewModelEntry =
          getViewModelEntryById(parentUuid);
      allViewModelEntries.add(parentViewModelEntry);

      if (!UUID.fromString(parentViewModelEntry.getParentUuid())
          .equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()))) {
        ViewModelEntry parentOfParentViewModelEntry =
            getViewModelEntryById(UUID.fromString(parentViewModelEntry.getParentUuid()));
        allViewModelEntries.add(0, parentOfParentViewModelEntry);
      }
    }
    return allViewModelEntries;
  }

  public List<ViewModelEntry> getAllViewModelEntriesForParent(UUID parentUuid) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();

    List<String> childrenUuids = eventDatabaseHelper
        .getAllDirectChildrenIdsForParent(parentUuid.toString());
    for (String childUuid : childrenUuids) {
      allViewModelEntries.add(eventDatabaseHelper.viewModelEntryFor(childUuid));
    }
    return allViewModelEntries;
  }

}
