package nl.freelist.data;

import android.content.ContentValues;
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

public class Repository {

  private static final String TAG = "Repository";

  private EventDatabaseHelper eventDatabaseHelper;

  public Repository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
  }


  public ResultObject<Entry> insert(Entry entry) {

    List<InsertQuery> insertQueryList = new ArrayList<>();

    int expectedEventSequenceNumberToSave =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(entry.getUuid().toString()) + 1;

    List<Event> newEventsToSave = entry.getEventList(expectedEventSequenceNumberToSave);
    for (Event event : newEventsToSave) {
      try {
        insertQueryList.addAll(eventDatabaseHelper.getQueriesForEvent("entry",
            expectedEventSequenceNumberToSave - 1 + newEventsToSave.indexOf(event), event));
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Entry entry)");
      }
    }
    ContentValues viewModelEntryContentValues = new ContentValues();
    viewModelEntryContentValues.put("uuid", entry.getUuid().toString());
    viewModelEntryContentValues.put("parentUuid", entry.getParentUuid().toString());
    viewModelEntryContentValues.put("ownerUuid", entry.getOwnerUuid().toString());
    viewModelEntryContentValues.put("title", entry.getTitle());
    viewModelEntryContentValues.put("description", entry.getDescription());
    viewModelEntryContentValues.put("duration", entry.getDuration());
    //Don't insert childrenCount and childrenDuration as they only get updated when ...
    viewModelEntryContentValues.put(
        "lastSavedEventSequenceNumber",
        expectedEventSequenceNumberToSave - 1 + newEventsToSave.size());

    insertQueryList.add(new InsertQuery("viewModelEntry", viewModelEntryContentValues));
    eventDatabaseHelper.executeInsertQueries(insertQueryList);

    return new ResultObject<Entry>(true);
  }


  public void insert(Iterable<Entry> items) {
  }


  public void update(Entry item) {
  }


  public void delete(Entry item) {
  }


  public void delete(Specifiable specification) {
  }


  public List<Entry> query(Specifiable specification) {
    List<Entry> allEntries = new ArrayList<>();
    return allEntries;
  }


  public Entry getById(String uuid) {
    EntryCreatedEvent entryCreatedEvent = eventDatabaseHelper.getEntryCreatedEvent(uuid);
    Entry entry = new Entry(UUID.fromString(entryCreatedEvent.getOwnerUuid()),
        UUID.fromString(entryCreatedEvent.getParentUuid()),
        UUID.fromString(entryCreatedEvent.getEntryId()), "", "", 0);
    return entry;
  }


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
