package nl.freelist.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.comparators.CalendarEntryComparator;
import nl.freelist.data.comparators.EventComparator;
import nl.freelist.data.dto.CalendarEntry;
import nl.freelist.data.dto.ViewModelCalendarOption;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.data.gson.Converters;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.entities.Calendar;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class Repository {

  private static final String TAG = "Repository";

  private EventDatabaseHelper eventDatabaseHelper;
  private SharedPreferences sharedPreferences;

  public Repository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  public List<sqlBundle> insert(Resource resource) {
    Log.d(TAG, "Repository insert called with entry " + resource.getUuid());

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    int lastSavedEventSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(resource.getUuid().toString());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    List<Event> newEventsToSave =
        resource.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");

    for (Event event : newEventsToSave) {
      try {
        sqlBundleList.addAll(
            eventDatabaseHelper.getQueriesForEvent(
                "resource",
                lastSavedEventSequenceNumber + newEventsToSave.indexOf(event),
                event));
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Resource resource):" + e.toString());
      }
    }
    return sqlBundleList;
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

    ContentValues viewModelEntryContentValues2 = new ContentValues();
    viewModelEntryContentValues2.put("uuid", entry.getUuid().toString());
    viewModelEntryContentValues2.put("parentUuid", entry.getParentUuid().toString());
    viewModelEntryContentValues2.put("ownerUuid", entry.getOwnerUuid().toString());
    viewModelEntryContentValues2.put("title", entry.getTitle());
    viewModelEntryContentValues2.put("description", entry.getDescription());
    viewModelEntryContentValues2.put("duration", entry.getDuration());
    viewModelEntryContentValues2.put(
        "lastSavedEventSequenceNumber", lastSavedEventSequenceNumber + newEventsToSave.size());
    sqlBundleList.add(new sqlBundle("viewModelEntry", viewModelEntryContentValues2));

    ContentValues viewModelEntryContentValues = new ContentValues();
    viewModelEntryContentValues.put("uuid", entry.getUuid().toString());
    viewModelEntryContentValues.put("parentUuid", entry.getParentUuid().toString());
    viewModelEntryContentValues.put("json", jsonOf(getViewModelEntryFrom(entry)));
    sqlBundleList.add(new sqlBundle("viewModelEntry2", viewModelEntryContentValues));

    return sqlBundleList;
  }

  private ViewModelEntry getViewModelEntryFrom(Entry entry) {
    ViewModelEntry viewModelEntry;
    viewModelEntry = new ViewModelEntry(
        entry.getOwnerUuid().toString(),
        entry.getParentUuid().toString(),
        entry.getUuid().toString(),
        entry.getTitle(),
        entry.getDescription(),
        entry.getDuration(),
        999,
        999,
        entry.getLastAppliedEventSequenceNumber()
    );
    return viewModelEntry;
  }

  private String jsonOf(ViewModelEntry viewModelEntry) {
    Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    return gson.toJson(viewModelEntry);
  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    eventDatabaseHelper.executeSqlBundles(sqlBundleList);
  }

  public Entry getEntryWithSavedEventsById(String uuid) {
    return eventDatabaseHelper.getEntryWithSavedEventsById(uuid);
  }

  public Resource getResourceWithSavedEventsById(String uuid) {
    return eventDatabaseHelper.getResourceWithSavedEventsById(uuid);
  }

  public ViewModelEntry getViewModelEntryById(UUID uuid) {
    return eventDatabaseHelper.viewModelEntryFor(uuid.toString());
  }

  public List<ViewModelEntry> getBreadcrumbViewModelEntries(UUID parentUuid) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    if (!parentUuid.equals(UUID.nameUUIDFromBytes(
        sharedPreferences.getString(Constants.SETTINGS_USER_UUID, null).getBytes()))) {
      ViewModelEntry parentViewModelEntry = getViewModelEntryById(parentUuid);
      if (parentViewModelEntry == null) {
        return allViewModelEntries;
      } else {
        allViewModelEntries.add(parentViewModelEntry);
      }

      if (!UUID.fromString(parentViewModelEntry.getParentUuid())
          .equals(UUID.nameUUIDFromBytes(
              sharedPreferences.getString(Constants.SETTINGS_USER_UUID, null).getBytes()))) {
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
        calendarEntryListSorted //Add date section headers
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

  public List<ViewModelEvent> getAllEventsForId(String uuid) {
    List<Event> eventList = eventDatabaseHelper.getEventsFor(uuid);
    Collections.sort(eventList, new EventComparator().reversed());

    List<ViewModelEvent> viewModelEventListSorted = new ArrayList<>();
    for (Event event : eventList) {
      String eventMessage;
      String entryId = "unknown";
      switch (event.getClass().getSimpleName()) {
        case "EntryCreatedEvent":
          eventMessage = "Created";
          EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
          entryId = entryCreatedEvent.getAggregateId();
          break;
        case "EntryDescriptionChangedEvent":
          eventMessage = "Description changed";
          EntryDescriptionChangedEvent entryDescriptionChangedEvent = (EntryDescriptionChangedEvent) event;
          entryId = entryDescriptionChangedEvent.getAggregateId();
          break;
        case "EntryDurationChangedEvent":
          eventMessage = "Duration changed";
          EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
          entryId = entryDurationChangedEvent.getAggregateId();
          break;
        case "EntryParentChangedEvent":
          eventMessage = "Parent changed";
          EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
          entryId = entryParentChangedEvent.getAggregateId(); //Todo doublecheck if not parentAfter
          break;
        case "EntryScheduledEvent":
          eventMessage = "Scheduled";
          EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
          entryId = entryScheduledEvent.getAggregateId();
          break;
        case "EntryTitleChangedEvent":
          eventMessage = "Title changed";
          EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
          entryId = entryTitleChangedEvent.getAggregateId();
          break;
        default:
          eventMessage = "Unrecognized: " + event.getClass().getSimpleName();
          break;
      }
      viewModelEventListSorted.add(
          new ViewModelEvent(event.getOccurredDateTime().toString(), entryId,
              eventMessage));
    }
    return viewModelEventListSorted;
  }

  public ViewModelCalendarOption getViewModelCalendarOptionFrom(Calendar calendar) {
    ViewModelCalendarOption viewModelCalendarOption = new ViewModelCalendarOption(
        calendar.getNumberOfProblems(),
        calendar.getNumberOfReschedules(),
        calendar.getResourceUuid().toString(),
        calendar.getLastScheduledEntryUuidString(),
        calendar.getEntryLastScheduledDateTimeRange(),
        calendar.getResourceLastAppliedEventSequenceNumber(),
        calendar.getEntryLastAppliedEventSequenceNumber(),
        calendar
    );
    return viewModelCalendarOption;
  }

  public List<ViewModelCalendarOption> getAllPrioOptions(String entryUuid, String resourceUuid) {
    Resource resource = getResourceWithSavedEventsById(resourceUuid);
    Entry entry = getEntryWithSavedEventsById(entryUuid);
    List<Calendar> allCalendars = resource.getSchedulingOptions(entry);
    List<ViewModelCalendarOption> allPrioEntries = new ArrayList<>();
    for (Calendar calendar : allCalendars) {
      allPrioEntries.add(getViewModelCalendarOptionFrom(calendar));
    }
    return allPrioEntries;
  }
}
