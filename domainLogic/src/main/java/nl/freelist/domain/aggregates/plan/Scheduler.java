package nl.freelist.domain.aggregates.plan;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.entry.EntryChildCountChangedEvent;
import nl.freelist.domain.events.entry.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryCreatedEvent;
import nl.freelist.domain.events.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.entry.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.entry.EntryScheduledEvent;
import nl.freelist.domain.events.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryTitleChangedEvent;
import nl.freelist.domain.events.person.SchedulerCreatedEvent;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.TimeSlot;
import nl.freelist.domain.valueObjects.constraints.Constraint;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class Scheduler {

  private static final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
  // Todo: When adding new Scheduler event or command:
  // Add to this.applyEvent(Event event) and if Entry event also to Entry.apply(Event event)
  // Add to EventDatabaseHelper.jsonOf(Event event)
  // Add to EventDatabaseHelper.getEventsFor(String uuid)
  // Possibly add to EventDatabaseHelper.getAdditionalQueriesForEvent(Event event, Entry entry,
  //      int eventSequenceNumber)
  // Add to ViewModelEntry dto
  // Add to EventDatabaseHelper.getViewModelEntryFrom(Entry entry)
  // Possibly List<ViewModelEvent> in Repository.getAllEventsForID(String uuid) (for UI history)

  private Id personId;
  private List<TimeSlot> scheduledSlots = new ArrayList<>();
  private List<TimeSlot> freeSlots = new ArrayList<>();
  private HashMap<Id, Entry> entriesMap = new HashMap<>();
  private int lastAppliedEventSequenceNumber;
  private List<Constraint> startAndDueConstraints = new ArrayList<>();
  private List<Constraint> timeBudgetConstraints = new ArrayList<>();
  private List<Constraint> repeatConstraints = new ArrayList<>();
  private List<Event> eventList = new ArrayList<>();

  public Scheduler(
      // Todo: make private and expose via public static method
      //  Scheduler.Create so validation can be included
  ) {
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(Level.INFO, "Scheduler initiated without events.");
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?

    if (event == null) {
      return;
    }
    String eventClass = event.getClass().getSimpleName();
    Entry entry;
    Id entryId;
    switch (eventClass) {
      // Todo: move all the checking to the upsert(entry) method
      // Rehydrating the Schedule/Entries should not have any validation or business logic as this
      // has already
      // happened! All of that should be in the commands that will emit event once state change is
      // ok to execute.
      case "SchedulerCreatedEvent":
        this.personId = ((SchedulerCreatedEvent) event).getPersonId();
        break;
      case "EntryCreatedEvent":
        entry = new Entry();
        entry.applyEvent(event);
        entriesMap.put(entry.getEntryId(), entry);
        break;
      case "EntryTitleChangedEvent":
        entryId = ((EntryTitleChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryNotesChangedEvent":
        entryId = ((EntryNotesChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryParentChangedEvent":
        entryId = ((EntryParentChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryDurationChangedEvent":
        entryId = ((EntryDurationChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryScheduledEvent":
        entryId = ((EntryScheduledEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryStartDateTimeChangedEvent":
        entryId = ((EntryStartDateTimeChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryEndDateTimeChangedEvent":
        entryId = ((EntryEndDateTimeChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryChildDurationChangedEvent":
        entryId = ((EntryChildDurationChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryChildCountChangedEvent":
        entryId = ((EntryChildCountChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      case "EntryPreferredDayConstraintsChangedEvent":
        entryId = ((EntryPreferredDayConstraintsChangedEvent) event).getEntryId();
        (entriesMap.get(entryId)).applyEvent(event);
        break;
      default:
        LOGGER.log(
            Level.WARNING,
            "Event can't be applied to Scheduler "
                + personId.toString()
                + " ; event type "
                + eventClass
                + " not recognized");
        break;
    }
    LOGGER.log(Level.INFO, eventClass + " applied");
    eventList.add(event);
    lastAppliedEventSequenceNumber += 1;
  }

  public Result upsert(
      Id entryId,
      Id parentId,
      String titleAfter,
      OffsetDateTime startDateTimeAfter,
      long durationAfter,
      OffsetDateTime endDateTimeAfter,
      String notesAfter,
      List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter,
      int lastSavedEventSequenceNumber) {
    try {
      OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
      Entry entry;

      // Optimistic locking only necessary in multi-user environment or if commands out of order
      // now only UI + all commands are scheduled sequentially on same thread

      // but if processing of changes that impact multiple entries takes longer than
      // what a user needs to navigate to a different entry
      // user could be making changes on stale data
      // avoid by greying out screen whilst still being able to navigate?
      // That't why it's still here
      if (lastSavedEventSequenceNumber != this.lastAppliedEventSequenceNumber) {
        throw new Exception(
            "Optimistic concurrency exception. Scheduler:"
                + this.lastAppliedEventSequenceNumber
                + " UI: "
                + lastSavedEventSequenceNumber);
      }

      if (entriesMap.get(entryId) == null) {
        createEntry(now, parentId, entryId);
      }
      entry = entriesMap.get(entryId);

      //checking takes place here, once an event is applied, it has ALREADY happened
      if (entry.getTitle() == null || !entry.getTitle().equals(titleAfter)) {
        //Todo: move to it's own bounded context - has nothing to do with scheduling invariants
        changeEntryTitle(now, entryId, titleAfter);
      }
      if ((startDateTimeAfter == null && entry.getStartDateTime() != null)
          || (startDateTimeAfter != null && !startDateTimeAfter.equals(entry.getStartDateTime()))) {
        changeEntryStartDateTime(now, entryId, startDateTimeAfter);
      }
      if (entry.getDuration() != durationAfter) {
        changeEntryDuration(now, entryId, durationAfter, entry);
      }
      if ((endDateTimeAfter == null && entry.getEndDateTime() != null)
          || (endDateTimeAfter != null && !endDateTimeAfter.equals(entry.getEndDateTime()))) {
        changeEntryEndDateTime(now, entryId, endDateTimeAfter);
      }
      if (entry.getNotes() == null || !entry.getNotes().equals(notesAfter)) {
        //Todo: move to it's own bounded context - has nothing to do with scheduling invariants
        changeEntryNotes(now, entryId, notesAfter);
      }
      if (impossibleDaysConstraintsAfter.size() != entry.getImpossibleDaysConstraints().size()) {
        changeImpossibleDayConstraints(now, entryId, impossibleDaysConstraintsAfter);
      }
      // Todo: add parentchange
      //    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent.Create(
      //      now,
      //      uuid,
      //        parentAfter
      //    );

    } catch (Exception e) {
      if (e.getMessage() != null) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }

  private void changeImpossibleDayConstraints(OffsetDateTime now, Id entryId,
      List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter) {
    EntryPreferredDayConstraintsChangedEvent entryPreferredDayConstraintsChangedEvent =
        EntryPreferredDayConstraintsChangedEvent.Create(
            now, personId, entryId, impossibleDaysConstraintsAfter);
    applyEvent(entryPreferredDayConstraintsChangedEvent);
  }

  private void changeEntryNotes(OffsetDateTime now, Id entryId, String notesAfter) {
    EntryNotesChangedEvent entryNotesChangedEvent =
        EntryNotesChangedEvent.Create(now, personId, entryId, notesAfter);
    applyEvent(entryNotesChangedEvent);

  }

  private void changeEntryEndDateTime(OffsetDateTime now, Id entryId,
      OffsetDateTime endDateTimeAfter) {
    EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent =
        EntryEndDateTimeChangedEvent.Create(now, personId, entryId, endDateTimeAfter);
    applyEvent(entryEndDateTimeChangedEvent);
  }

  private void changeEntryDuration(
      OffsetDateTime now, Id entryId, long durationAfter, Entry entry) {
    EntryDurationChangedEvent entryDurationChangedEvent =
        EntryDurationChangedEvent.Create(now, personId, entryId, durationAfter);
    Long durationDelta = durationAfter - entry.getDuration();
    applyEvent(entryDurationChangedEvent);
    Id originEntryId = entryId;
    Id loopEntryId = entryId;
    Id parentId;
    while (!entriesMap.get(loopEntryId).getParentEntryId().equals(personId)
        && !entriesMap
        .get(loopEntryId)
        .getParentEntryId()
        .equals(originEntryId)) { // avoid circular reference
      parentId = entriesMap.get(loopEntryId).getParentEntryId();
      EntryChildDurationChangedEvent entryChildDurationChangedEvent =
          EntryChildDurationChangedEvent.Create(
              OffsetDateTime.now(ZoneOffset.UTC), parentId, personId, durationDelta);
      applyEvent(entryChildDurationChangedEvent);
      loopEntryId = parentId;
    }
  }

  private void changeEntryStartDateTime(
      OffsetDateTime now, Id entryId, OffsetDateTime startDateTimeAfter) {
    EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent =
        EntryStartDateTimeChangedEvent.Create(now, personId, entryId, startDateTimeAfter);
    applyEvent(entryStartDateTimeChangedEvent);
  }

  private void changeEntryTitle(OffsetDateTime now, Id entryId, String titleAfter) {
    EntryTitleChangedEvent entryTitleChangedEvent =
        EntryTitleChangedEvent.Create(now, personId, entryId, titleAfter);
    applyEvent(entryTitleChangedEvent);
  }

  private void createEntry(OffsetDateTime now, Id parentId, Id entryId) {
    EntryCreatedEvent entryCreatedEvent;
    entryCreatedEvent = EntryCreatedEvent.Create(now, personId, parentId, entryId);
    applyEvent(entryCreatedEvent);
    // update parents recursively
    // Todo: make multiple parents possible
    Id originEntryId = entryId;
    Id loopEntryId = entryId;
    while (!entriesMap.get(loopEntryId).getParentEntryId().equals(personId)
        && !entriesMap
        .get(loopEntryId)
        .getParentEntryId()
        .equals(originEntryId)) { // avoid circular reference
      parentId = entriesMap.get(loopEntryId).getParentEntryId();
      EntryChildCountChangedEvent entryChildCountChangedEvent =
          EntryChildCountChangedEvent.Create(
              OffsetDateTime.now(ZoneOffset.UTC), parentId, personId, 1);
      applyEvent(entryChildCountChangedEvent);
      loopEntryId = parentId;
    }
  }

  public List<Event> getListOfEventsWithSequenceHigherThan(int fromEventSequenceNumber) {
    fromEventSequenceNumber += 1;
    return eventList.subList(fromEventSequenceNumber, eventList.size());
  }

  public void applyEvents(List<Event> eventList) {
    for (Event event : eventList) {
      applyEvent(event);
    }
  }

  public int getLastAppliedEventSequenceNumber() {
    return lastAppliedEventSequenceNumber;
  }

  public Id getPersonId() {
    return personId;
  }

  public List<Event> getEventList() {
    return eventList;
  }

  public Event getPreviousOf(Event event, int eventSequenceNumberToCountdownFrom) {
    String eventTypeToFind = event.getClass().getSimpleName();

    LOGGER.log(
        Level.INFO,
        "Asked for the previous event of "
            + eventTypeToFind
            + " with eventSequenceNumberToCountdownFrom lower than "
            + eventSequenceNumberToCountdownFrom);
    if (eventSequenceNumberToCountdownFrom
        > (eventList.size() - 1)) { // eventSequenceNumbers start at 0 - just like List
      LOGGER.log(
          Level.WARNING,
          "Asked to count down from "
              + eventSequenceNumberToCountdownFrom
              + " but max eventSequenceNumber of eventList is "
              + (eventList.size() - 1));
      return null;
    }

    for (int eventSequenceNumberCountdown =
        eventSequenceNumberToCountdownFrom - 1; // Exclude the event to compare with
        eventSequenceNumberCountdown >= 0;
        eventSequenceNumberCountdown--) {
      System.out.println(eventSequenceNumberCountdown);
      Event eventToTest = eventList.get(eventSequenceNumberCountdown);
      if (eventToTest.getClass().getSimpleName().equals(eventTypeToFind)) {
        LOGGER.log(
            Level.INFO,
            "Returning Event with sequence number "
                + eventSequenceNumberToCountdownFrom
                + " of type "
                + eventTypeToFind);
        return eventToTest;
      }
    }
    return null;
  }

  public List<Entry> getEntries() {
    return new ArrayList<Entry>(entriesMap.values());
  }
}
