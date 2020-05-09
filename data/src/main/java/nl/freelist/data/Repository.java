package nl.freelist.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.entities.Calendar;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Person;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class Repository {
  //Todo: transform repository to an interface which EventDatabasehelper implements
  //  + figure out if transactions should be supported from Command level or from Repo implementation level
  //    intuition is that only one aggregate command can be called at a time, that is the scope of atomic changes

  private static final String TAG = "Repository";

  private EventDatabaseHelper eventDatabaseHelper;
  private SharedPreferences sharedPreferences;

  public Repository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  public List<sqlBundle> insert(Person person) { //Todo: fix like insert(entry)
    Log.d(TAG, "Repository insert called with entry " + person.getUuid());

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    int lastSavedEventSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(person.getUuid().toString());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    List<Event> newEventsToSave =
        person.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");

    int eventSequenceNumberForQuery = lastSavedEventSequenceNumber;

    for (Event event : newEventsToSave) {
      try {
        sqlBundleList.addAll(
            eventDatabaseHelper.getInitialQueriesForEvent(
                "person",
                eventSequenceNumberForQuery,
                event)
        );
        eventSequenceNumberForQuery += 1;
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Person person):" + e.toString());
      }
    }
    return sqlBundleList;
  }

  public void insert(Entry entry)
      throws Exception {
    Log.i(TAG, "Repository insert called with entry " + entry.getUuid());
    eventDatabaseHelper.insert(entry);
  }

  public Entry getEntryWithSavedEventsById(String uuid) {
    return eventDatabaseHelper.getEntryWithSavedEventsById(uuid);
  }

  public Person getResourceWithSavedEventsById(String uuid) {
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
        if (parentOfParentViewModelEntry == null) {
          return allViewModelEntries;
        } else {
          allViewModelEntries.add(0, parentOfParentViewModelEntry);
        }
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
      String entryId = event.getAggregateId();
      switch (event.getClass().getSimpleName()) {
        case "EntryCreatedEvent":
          eventMessage = "Created";
          EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
          break;
        case "EntryNotesChangedEvent":
          eventMessage = "Description changed";
          EntryNotesChangedEvent entryNotesChangedEvent = (EntryNotesChangedEvent) event;
          break;
        case "EntryDurationChangedEvent":
          eventMessage = "Duration changed";
          EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
          break;
        case "EntryParentChangedEvent":
          eventMessage = "Parent changed";
          EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
          break;
        case "EntryScheduledEvent":
          eventMessage = "Scheduled";
          EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
          break;
        case "EntryTitleChangedEvent":
          eventMessage = "Title changed";
          EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
          break;
        case "EntryStartDateTimeChangedEvent":
          eventMessage = "Start changed";
          EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent = (EntryStartDateTimeChangedEvent) event;
          break;
        case "EntryEndDateTimeChangedEvent":
          eventMessage = "Start changed";
          EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent = (EntryEndDateTimeChangedEvent) event;
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

  public ViewModelCalendarOption getViewModelCalendarOptionFrom(
      Calendar calendar) { //Todo: refactor to go through aggregate root
    ViewModelCalendarOption viewModelCalendarOption = new ViewModelCalendarOption(
        calendar.getNumberOfProblems(),
        calendar.getNumberOfReschedules(),
        calendar.getCalendarUuid().toString(),
        calendar.getLastScheduledEntryUuidString(),
        calendar.getEntryLastScheduledDateTimeRange(),
        calendar.getResourceLastAppliedEventSequenceNumber(),
        calendar.getEntryLastAppliedEventSequenceNumber(),
        calendar
    );
    return viewModelCalendarOption;
  }

  public List<ViewModelCalendarOption> getAllPrioOptions(String entryUuid, String resourceUuid) {
    Person person = getResourceWithSavedEventsById(resourceUuid);
    Entry entry = getEntryWithSavedEventsById(entryUuid);
    List<Calendar> allCalendars = person.getSchedulingOptions(entry);
    List<ViewModelCalendarOption> allPrioEntries = new ArrayList<>();
    for (Calendar calendar : allCalendars) {
      allPrioEntries.add(getViewModelCalendarOptionFrom(calendar));
    }
    return allPrioEntries;
  }
}
