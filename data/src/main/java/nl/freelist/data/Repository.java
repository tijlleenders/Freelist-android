package nl.freelist.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import io.reactivex.Completable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.comparators.CalendarEntryComparator;
import nl.freelist.data.dto.CalendarEntry;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.ResourceCreatedEvent;
import nl.freelist.domain.valueObjects.Email;

public class Repository {

  private static final String TAG = "Repository";

  private EventDatabaseHelper eventDatabaseHelper;

  public Repository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
    eventDatabaseHelper.tempFillViewModelCalendar();
  }

  public List<sqlBundle> insert(Entry entry) {
    Log.d(TAG, "Repository insert called with entry " + entry.getUuid());

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    int lastSavedEventSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(entry.getUuid().toString());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    List<Event> newEventsToSave =
        entry.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");
    for (Event event : newEventsToSave) {
      try {
        sqlBundleList.addAll(
            eventDatabaseHelper.getQueriesForEvent(
                "entry", lastSavedEventSequenceNumber + newEventsToSave.indexOf(event), event));
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Entry entry):" + e.toString());
      }
    }

    ContentValues viewModelEntryContentValues = new ContentValues();
    viewModelEntryContentValues.put("uuid", entry.getUuid().toString());
    viewModelEntryContentValues.put("parentUuid", entry.getParentUuid().toString());
    viewModelEntryContentValues.put("ownerUuid", entry.getOwnerUuid().toString());
    viewModelEntryContentValues.put("title", entry.getTitle());
    viewModelEntryContentValues.put("description", entry.getDescription());
    viewModelEntryContentValues.put("duration", entry.getDuration());
    viewModelEntryContentValues.put(
        "lastSavedEventSequenceNumber", lastSavedEventSequenceNumber + newEventsToSave.size());

    sqlBundleList.add(new sqlBundle("viewModelEntry", viewModelEntryContentValues));

    return sqlBundleList;
  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    eventDatabaseHelper.executeSqlBundles(sqlBundleList);
  }

  public Entry getById(String uuid) {
    EntryCreatedEvent entryCreatedEvent = eventDatabaseHelper.getEntryCreatedEvent(uuid);
    Entry entry =
        new Entry(
            UUID.fromString(entryCreatedEvent.getOwnerUuid()),
            UUID.fromString(entryCreatedEvent.getParentUuid()),
            UUID.fromString(entryCreatedEvent.getEntryId()),
            "",
            "",
            0);
    return entry;
  }

  public Resource getResourceById(String uuid) {
    ResourceCreatedEvent resourceCreatedEvent = eventDatabaseHelper.getResourceCreatedEvent(uuid);
    Resource resource =
        Resource.Create(new Email("testEmail"), "testResource");
    return resource;
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
      ViewModelEntry parentViewModelEntry = getViewModelEntryById(parentUuid);
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

    List<String> childrenUuids =
        eventDatabaseHelper.getAllDirectChildrenIdsForParent(parentUuid.toString());
    for (String childUuid : childrenUuids) {
      allViewModelEntries.add(eventDatabaseHelper.viewModelEntryFor(childUuid));
    }
    return allViewModelEntries;
  }

  public List<CalendarEntry> getAllCalendarEntriesForOwner(UUID fromString) {
    List<CalendarEntry> calendarEntryList = eventDatabaseHelper.getAllCalendarEntries();
    Collections.sort(calendarEntryList, new CalendarEntryComparator());

    List<CalendarEntry> calendarEntryListSorted = new ArrayList<>();
    String date = "";
    for (CalendarEntry calendarEntry : calendarEntryList) {
      if (!calendarEntry.getDate().equals(date)) {
        date = calendarEntry.getDate();
        calendarEntryListSorted
            .add(new CalendarEntry("", date, Constants.CALENDAR_ENTRY_DATE_VIEW_TYPE
                , "", "", ""));
      }
      calendarEntryListSorted.add(calendarEntry);
    }

    return calendarEntryListSorted;
  }

  public Boolean deleteAllEntriesFromRepository() {
    eventDatabaseHelper.deleteAllEntriesFromRepository();
    Boolean result = new Boolean(true);
    return result;
  }

  public Completable scheduleEntry(String uuid, String resource) {

    return Completable.error(new Throwable());
  }

  public List<ViewModelEvent> getAllEventsForId(String uuid) {
    List<Event> eventList = eventDatabaseHelper.getEventsFor(uuid);
    //Todo: sort events by time
    //Collections.sort(calendarEntryList, new CalendarEntryComparator());

    List<ViewModelEvent> viewModelEventListSorted = new ArrayList<>();
    for (Event event : eventList) {
      //Todo: checkout sort loop in for calendarEntry
      String eventMessage;
      switch (event.getClass().getSimpleName()) {
        case "EntryCreatedEvent":
          eventMessage = "Created";
          break;
        case "EntryDescriptionChangedEvent":
          eventMessage = "Description changed";
          break;
        case "EntryDurationChangedEvent":
          eventMessage = "Duration changed";
          break;
        case "EntryParentChangedEvent":
          eventMessage = "Parent changed";
          break;
        case "EntryScheduledEvent":
          eventMessage = "Scheduled";
          break;
        case "EntryTitleChangedEvent":
          eventMessage = "Title changed";
          break;
        default:
          eventMessage = "Unrecognized: " + event.getClass().getSimpleName();
          break;
      }
      viewModelEventListSorted.add(
          new ViewModelEvent(event.getOccurredDateTime().toString(), event.getEntryId(),
              eventMessage));
    }
    return viewModelEventListSorted;
  }
}
