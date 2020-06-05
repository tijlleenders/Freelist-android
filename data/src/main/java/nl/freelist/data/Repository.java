package nl.freelist.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nl.freelist.data.comparators.EventComparator;
import nl.freelist.data.dto.ViewModelEntries;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.domain.aggregates.Person;
import nl.freelist.domain.aggregates.scheduler.Scheduler;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.scheduler.calendar.EntryScheduledEvent;
import nl.freelist.domain.events.scheduler.entry.EntryCreatedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryTitleChangedEvent;
import nl.freelist.domain.valueObjects.Id;

public class Repository {
  // Todo: transform repository to an interface which EventDatabasehelper implements
  //  + figure out if transactions should be supported from Command level or from Repo
  // implementation level
  //    intuition is that only one aggregate command can be called at a time, that is the scope of
  // atomic changes

  private static final String TAG = "Repository";

  private EventDatabaseHelper eventDatabaseHelper;
  private SharedPreferences sharedPreferences;

  public Repository(Context appContext) {
    eventDatabaseHelper = EventDatabaseHelper.getInstance(appContext);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  public void insert(Scheduler scheduler) throws Exception {
    Log.i(TAG, "Repository insert called with scheduler " + scheduler.getPersonId());
    eventDatabaseHelper.insert(scheduler);
  }

  public Scheduler getSchedulerWithEventsById(Id personId) {
    return eventDatabaseHelper.getSchedulerWithSavedEventsById(personId);
  }

  public Person getPersonWithSavedEventsById(Id personId) {
    return eventDatabaseHelper.getPersonWithSavedEventsById(personId);
  }

  public ViewModelEntry getViewModelEntryById(Id entryId) {
    return eventDatabaseHelper.viewModelEntryFor(entryId.toString());
  }

  public ViewModelEntries getViewModelEntries(String personId) {
    List<ViewModelEntry> viewModelEntryList = eventDatabaseHelper.getViewModelEntries(personId);
    int lastAppliedSchedulerSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(Id.fromString(personId));
    ViewModelEntries result =
        new ViewModelEntries(viewModelEntryList, personId, lastAppliedSchedulerSequenceNumber);
    return result;
  }

  public ViewModelEntries getViewModelEntriesForCalendar(String personId) {
    List<ViewModelEntry> viewModelEntryList = eventDatabaseHelper.getViewModelEntries(personId);
    int lastAppliedSchedulerSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(Id.fromString(personId));
    List<ViewModelEntry> tempList = new ArrayList<>(viewModelEntryList);
    HashSet<OffsetDateTime> datesToAdd = new HashSet<>();
    for (ViewModelEntry entry : viewModelEntryList) {
      if (entry.getDuration() == 0 || entry.getChildrenCount() > 0) {
        tempList.remove(entry);
      } else {
        datesToAdd.add(entry.getScheduledStartDateTime().truncatedTo(ChronoUnit.DAYS));
      }
    }
    // Todo: Add date entries
    for (OffsetDateTime dateToAdd : datesToAdd) {
      ViewModelEntry dateEntry =
          new ViewModelEntry(
              Id.Create(),
              Id.Create(),
              Id.Create(),
              "",
              null,
              0L,
              null,
              null,
              "",
              0L,
              0L,
              dateToAdd,
              null,
              -1
          );
      tempList.add(dateEntry);
    }
    tempList.sort(
        (o1, o2) -> { // add comparator logic for date label entries
          if (o1.getScheduledStartDateTime().isBefore(o2.getScheduledStartDateTime())) {
            return -1;
          }
          return 0;
        });
    ViewModelEntries result =
        new ViewModelEntries(tempList, personId, lastAppliedSchedulerSequenceNumber);
    return result;
  }

  public Boolean deleteAllEntriesFromRepository() {
    eventDatabaseHelper.deleteAllEntriesFromRepository();
    Boolean result = new Boolean(true);
    return result;
  }

  public List<ViewModelEvent> getAllEventsForId(
      Id uuid) { // Todo: implement with document-based viewModel
    List<Event> eventList = eventDatabaseHelper.getEventsFor(uuid);
    Collections.sort(eventList, new EventComparator().reversed());

    List<ViewModelEvent> viewModelEventListSorted = new ArrayList<>();
    for (Event event : eventList) {
      String eventMessage;
      Id entryId = event.getAggregateId();
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
          EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent =
              (EntryStartDateTimeChangedEvent) event;
          break;
        case "EntryEndDateTimeChangedEvent":
          eventMessage = "Start changed";
          EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent =
              (EntryEndDateTimeChangedEvent) event;
          break;
        default:
          eventMessage = "Unrecognized: " + event.getClass().getSimpleName();
          break;
      }
      viewModelEventListSorted.add(
          new ViewModelEvent(
              event.getOccurredDateTime().toString(), entryId.toString(), eventMessage));
    }
    return viewModelEventListSorted;
  }
}
