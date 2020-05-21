package nl.freelist.domain.aggregates.scheduler;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.scheduler.SchedulerCreatedEvent;
import nl.freelist.domain.events.scheduler.calendar.EntryNotScheduledEvent;
import nl.freelist.domain.events.scheduler.calendar.EntryScheduledEvent;
import nl.freelist.domain.events.scheduler.entry.EntryChildCountChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryCreatedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryTitleChangedEvent;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.TimeSlot;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class Scheduler {

  private static final Logger LOGGER = Logger.getLogger(Scheduler.class.getName());
  // Todo: When adding new Scheduler event or command:
  // Only Scheduler.applyEvent has the right to call Entry/Calendar.applyEvent
  // Add to this.applyEvent(Event event) and if Entry event also to Entry.apply(Event event)
  // Add to EventDatabaseHelper.jsonOf(Event event)
  // Add to EventDatabaseHelper.getEventsFor(String uuid)
  // Possibly add to EventDatabaseHelper.getAdditionalQueriesForEvent(Event event, Entry entry,
  //      int eventSequenceNumber)
  // Add to ViewModelEntry dto
  // Add to EventDatabaseHelper.getViewModelEntryFrom(Entry entry)
  // Possibly List<ViewModelEvent> in Repository.getAllEventsForID(String uuid) (for UI history)

  private Id personId;
  private HashMap<Id, Entry> entriesMap = new HashMap<>();
  private Calendar calendar;
  private List<Event> eventList = new ArrayList<>();
  private int lastAppliedEventSequenceNumber;

  private Scheduler(
      // Todo: make private and expose via public static method
      //  Scheduler.Create so validation can be included
  ) {
    LOGGER.log(Level.INFO, "Scheduler initiated without events.");
  }

  public static Scheduler Create(Id personId) {
    Scheduler scheduler = new Scheduler();
    scheduler.personId = personId;
    scheduler.lastAppliedEventSequenceNumber = -1;
    scheduler.calendar = Calendar.Create();
    return scheduler;
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
      case "EntryScheduledEvent":
        LOGGER.log(Level.INFO, "EntryScheduledEvent applied to person");
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        // apply to calendar, and also to Entry - it is ok as for the outside world it is only
        // applied to Scheduler aggregate
        calendar.applyEvent(entryScheduledEvent);
        entriesMap.get(entryScheduledEvent.getEntryId()).applyEvent(entryScheduledEvent);
        break;
      case "EntryNotScheduledEvent":
        EntryNotScheduledEvent entryNotScheduledEvent = (EntryNotScheduledEvent) event;
        // apply to calendar, and also to Entry - it is ok as for the outside world it is only
        // applied to Scheduler aggregate
        calendar.applyEvent(entryNotScheduledEvent);
        entriesMap.get(entryNotScheduledEvent.getEntryId()).applyEvent(entryNotScheduledEvent);
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
      OffsetDateTime startAtOrAfterDateTimeAfter,
      long durationAfter,
      OffsetDateTime finishAtOrBeforeDateTimeAfter,
      String notesAfter,
      List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter,
      int lastSavedEventSequenceNumber) {
    try {
      OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
      Boolean rescheduleFlag = false;

      if (startAtOrAfterDateTimeAfter == null) {
        startAtOrAfterDateTimeAfter = now;
      }
      if (finishAtOrBeforeDateTimeAfter == null) {
        finishAtOrBeforeDateTimeAfter = Constants.END_OF_TIME;
      }
      Entry entry;
      checkLastSavedEventSequenceNumber(lastSavedEventSequenceNumber);

      if (entriesMap.get(entryId) == null) {
        createEntry(now, parentId, entryId);
      }
      entry = entriesMap.get(entryId);

      // checking takes place here, once an event is applied, it has ALREADY happened
      if (entry.isNewTitle(
          titleAfter)) { // Todo: move to it's own bounded context - has nothing to do with
        // scheduling invariants
        changeEntryTitle(now, entryId, titleAfter);
      }
      if (entry.isNewStartAtOfAfterDateTime(startAtOrAfterDateTimeAfter)) {
        rescheduleFlag = true;
        changeEntryStartDateTime(now, entryId, startAtOrAfterDateTimeAfter);
      }
      if (entry.isNewFinishAtOrBeforeDateTimeAfter(finishAtOrBeforeDateTimeAfter)) {
        rescheduleFlag = true;
        changeEntryEndDateTime(now, entryId, finishAtOrBeforeDateTimeAfter);
      }
      if (entry.isNewNotes(
          notesAfter)) { // Todo: move to it's own bounded context - has nothing to do with
        // scheduling invariants
        changeEntryNotes(now, entryId, notesAfter);
      }
      if (entry.isNewImpossibleDaysConstraint(impossibleDaysConstraintsAfter)) {
        rescheduleFlag = true;
        changeImpossibleDayConstraints(now, entryId, impossibleDaysConstraintsAfter);
      }
      // Todo: add parentchange
      if (entry.isNewDuration(durationAfter)) {
        rescheduleFlag = true;
        changeEntryDuration(now, entryId, durationAfter, entry);
      }
      if (rescheduleFlag) {
        entry = entriesMap.get(entryId);
        scheduleEntry(entry);
      }
    } catch (Exception e) {
      if (e.getMessage() != null) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }

  private void checkLastSavedEventSequenceNumber(int lastSavedEventSequenceNumber)
      throws Exception {
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
  }

  public Calendar getCalendar() {
    return calendar;
  }


  private void scheduleEntry(Entry entry) {
    TimeSlot scheduledTimeSlot = null;
    TimeSlot freeTimeSlotToDelete = null;
    List<TimeSlot> freeTimeSlotsToCreate = new ArrayList<>();
    OffsetDateTime scheduledStartDateTime = null;

    TimeSlot compatibleFreeTimeSlot =
        calendar.getCompatibleFreeTimeSlots(
            entry.getDuration(),
            entry.getStartAtOrAfterDateTime(),
            entry.getFinishAtOrBeforeDateTime());
    if (compatibleFreeTimeSlot == null) {
      System.out.println("No compatible freeTimeSlot found");
      EntryNotScheduledEvent entryNotScheduledEvent =
          EntryNotScheduledEvent.Create(
              OffsetDateTime.now(ZoneOffset.UTC),
              personId,
              entry.getPersonId());
      applyEvent(entryNotScheduledEvent);
    } else {
      freeTimeSlotToDelete = calendar.getFreeTimeSlot(compatibleFreeTimeSlot.getKey());

      if (compatibleFreeTimeSlot.getStartDateTime().toEpochSecond() >= entry
          .getStartAtOrAfterDateTime().toEpochSecond()) {
        scheduledStartDateTime = compatibleFreeTimeSlot.getStartDateTime();
      } else {
        scheduledStartDateTime = entry.getStartAtOrAfterDateTime();
      }
      scheduledTimeSlot =
          TimeSlot.Create(
              scheduledStartDateTime,
              scheduledStartDateTime.plusSeconds(entry.getDuration()),
              entry.getEntryId());
      freeTimeSlotsToCreate = compatibleFreeTimeSlot.minus(scheduledTimeSlot);
      EntryScheduledEvent entryScheduledEvent =
          EntryScheduledEvent.Create(
              OffsetDateTime.now(ZoneOffset.UTC),
              entry.getPersonId(),
              scheduledTimeSlot,
              freeTimeSlotToDelete,
              freeTimeSlotsToCreate);
      applyEvent(entryScheduledEvent);
    }
  }

  private void changeImpossibleDayConstraints(
      OffsetDateTime now,
      Id entryId,
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

  private void changeEntryEndDateTime(
      OffsetDateTime now, Id entryId, OffsetDateTime endDateTimeAfter) {
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

  public List<Entry> getEntries() {
    return new ArrayList<>(entriesMap.values());
  }
}
