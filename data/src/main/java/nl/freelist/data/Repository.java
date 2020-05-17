package nl.freelist.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import nl.freelist.data.comparators.CalendarEntryComparator;
import nl.freelist.data.comparators.EventComparator;
import nl.freelist.data.dto.ViewModelAppointment;
import nl.freelist.data.dto.ViewModelEntries;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.domain.aggregates.Person;
import nl.freelist.domain.aggregates.plan.Scheduler;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.entry.EntryCreatedEvent;
import nl.freelist.domain.events.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.entry.EntryScheduledEvent;
import nl.freelist.domain.events.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryTitleChangedEvent;
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

  public List<sqlBundle> insert(Person person) { // Todo: fix like insert(entry)
    Log.d(TAG, "Repository insert called with entry " + person.getPersonId());

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    int lastSavedEventSequenceNumber =
        eventDatabaseHelper.selectLastSavedEventSequenceNumber(person.getPersonId());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);


    List<Event> newEventsToSave =
        person.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");

    int eventSequenceNumberForQuery = lastSavedEventSequenceNumber;

    for (Event event : newEventsToSave) {
      try {
        sqlBundleList.addAll(
            eventDatabaseHelper.getInitialQueriesForEvent(
                "person", eventSequenceNumberForQuery, event));
        eventSequenceNumberForQuery += 1;
      } catch (Exception e) {
        Log.d(TAG, "Error while executing insert(Person person):" + e.toString());
      }
    }
    return sqlBundleList;
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

  public ViewModelEntries getViewModelEntriesForParent(String parentId, String personId) {
    List<ViewModelEntry> viewModelEntryList = eventDatabaseHelper
        .getAllViewModelEntriesForParent(parentId);
    int lastAppliedSchedulerSequenceNumber = eventDatabaseHelper
        .selectLastSavedEventSequenceNumber(Id.fromString(personId));
    ViewModelEntries result = new ViewModelEntries(viewModelEntryList,
        lastAppliedSchedulerSequenceNumber);
    return result;
  }

  public List<ViewModelEntry> getBreadcrumbViewModelEntries(Id parentEntryId) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    if (!parentEntryId.equals(
        sharedPreferences.getString(Constants.SETTINGS_USER_UUID, null))) {
      ViewModelEntry parentViewModelEntry = getViewModelEntryById(parentEntryId);
      if (parentViewModelEntry == null) {
        return allViewModelEntries;
      } else {
        allViewModelEntries.add(parentViewModelEntry);
      }

      if (!parentViewModelEntry.getParentEntryId()
          .equals(
              sharedPreferences.getString(Constants.SETTINGS_USER_UUID, null))) {
        ViewModelEntry parentOfParentViewModelEntry =
            getViewModelEntryById(Id.fromString(parentViewModelEntry.getParentEntryId()));
        if (parentOfParentViewModelEntry == null) {
          return allViewModelEntries;
        } else {
          allViewModelEntries.add(0, parentOfParentViewModelEntry);
        }
      }
    }
    return allViewModelEntries;
  }


  public List<ViewModelAppointment> getAllCalendarEntriesForOwner(String fromString) {
    List<ViewModelAppointment> viewModelAppointmentList =
        eventDatabaseHelper.getAllCalendarEntries();
    Collections.sort(viewModelAppointmentList, new CalendarEntryComparator());

    List<ViewModelAppointment> viewModelAppointmentListSorted = new ArrayList<>();
    String date = "";
    for (ViewModelAppointment viewModelAppointment : viewModelAppointmentList) {
      if (!viewModelAppointment.getDate().equals(date)) {
        date = viewModelAppointment.getDate();
        viewModelAppointmentListSorted // Add date section headers
            .add(
                new ViewModelAppointment(
                    "", "", date, Constants.CALENDAR_ENTRY_DATE_VIEW_TYPE, date, "", ""));
      }
      viewModelAppointmentListSorted.add(viewModelAppointment);
    }

    return viewModelAppointmentListSorted;
  }

  public Boolean deleteAllEntriesFromRepository() {
    eventDatabaseHelper.deleteAllEntriesFromRepository();
    Boolean result = new Boolean(true);
    return result;
  }

  public List<ViewModelEvent> getAllEventsForId(
      Id uuid) { //Todo: implement with document-based viewModel
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

  // Todo: refactor later (in EventDatabaseHelper), options not in MVP
  //  public ViewModelCalendarOption getViewModelCalendarOptionFrom(
  //      Calendar calendar) { //Todo: refactor to go through aggregate root
  //    ViewModelCalendarOption viewModelCalendarOption = new ViewModelCalendarOption(
  //        calendar.getNumberOfProblems(),
  //        calendar.getNumberOfReschedules(),
  //        calendar.getCalendarUuid().toString(),
  //        calendar.getLastScheduledEntryUuidString(),
  //        calendar.getEntryLastScheduledDateTimeRange(),
  //        calendar.getResourceLastAppliedEventSequenceNumber(),
  //        calendar.getEntryLastAppliedEventSequenceNumber(),
  //        calendar
  //    );
  //    return viewModelCalendarOption;
  //  }



}
