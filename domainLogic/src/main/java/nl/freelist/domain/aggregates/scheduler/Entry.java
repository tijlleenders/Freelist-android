package nl.freelist.domain.aggregates.scheduler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
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
import nl.freelist.domain.valueObjects.constraints.Constraint;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class Entry {

  private static final Logger LOGGER = Logger.getLogger(Entry.class.getName());
  // Todo: When adding new event for Entry, or another aggregate/entitiy:
  // Add to SaveEntryCommand
  // Add to this.applyEvent(Event event)
  // Add to EventDatabaseHelper.jsonOf(Event event)
  // Add to EventDatabaseHelper.getEventsFor(String uuid)
  // Add to Entry ViewModelEntry dto - if applicable
  // Add to EventDatabaseHelper.getViewModelEntryFrom(Entry entry) - if applicable
  // List<ViewModelEvent> in Repository.getAllEventsForID(String uuid) (for UI history) - if
  // applicable

  private Id personId;
  private Id entryId;
  private Id parentEntryId;
  private List<Id> parents;
  private List<Id> children;
  private String title = "";
  private OffsetDateTime startAtOrAfterDateTime;
  private long duration = 0;
  private OffsetDateTime finishAtOrBeforeDateTime;
  private List<ImpossibleDaysConstraint> impossibleDaysConstraints = new ArrayList<>();
  private String notes = "";
  private long childCount; // are applied within same transaction that add descendants
  private long
      childDuration; // are applied within same transaction that changes descendant duration
  private List<Constraint> startAndDueConstraints = new ArrayList<>();
  private List<Constraint> timeBudgetConstraints = new ArrayList<>();
  private List<Constraint> repeatConstraints = new ArrayList<>();
  private List<Event> eventList =
      new ArrayList<>(); // needed as all events are stored on Scheduler? leave for now as helps in
  // debugging
  private OffsetDateTime scheduledStartDateTime = null;
  private OffsetDateTime scheduledEndDateTime = null;

  public Entry(
      // Todo: make private and expose via public static method
      //  Entry.Create so validation can be included
      ) {
    LOGGER.log(Level.INFO, "Entry initiated without events.");
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?

    if (event == null) {
      return;
    }
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "EntryCreatedEvent":
        EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
        this.entryId = entryCreatedEvent.getEntryId();
        this.personId = entryCreatedEvent.getAggregateId();
        this.parentEntryId = entryCreatedEvent.getParentEntryId();
        break;
      case "EntryTitleChangedEvent":
        EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
        this.title = entryTitleChangedEvent.getTitleAfter();
        break;
      case "EntryNotesChangedEvent":
        EntryNotesChangedEvent entryNotesChangedEvent = (EntryNotesChangedEvent) event;
        this.notes = entryNotesChangedEvent.getNotesAfter();
        break;
      case "EntryParentChangedEvent":
        EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
        this.parentEntryId = entryParentChangedEvent.getParentAfter();
        break;
      case "EntryDurationChangedEvent":
        EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
        this.duration = entryDurationChangedEvent.getDurationAfter();
        break;
      case "EntryScheduledEvent":
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        scheduledStartDateTime = entryScheduledEvent.getScheduledTimeSlot().getStartDateTime();
        scheduledEndDateTime = entryScheduledEvent.getScheduledTimeSlot().getEndDateTime();
        break;
      case "EntryNotScheduledEvent":
        scheduledStartDateTime = null;
        scheduledEndDateTime = null;
        break;
      case "EntryStartDateTimeChangedEvent":
        EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent =
            (EntryStartDateTimeChangedEvent) event;
        this.startAtOrAfterDateTime = entryStartDateTimeChangedEvent.getStartDateTimeAfter();
        break;
      case "EntryEndDateTimeChangedEvent":
        EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent =
            (EntryEndDateTimeChangedEvent) event;
        this.finishAtOrBeforeDateTime = entryEndDateTimeChangedEvent.getEndDateTimeAfter();
        break;
      case "EntryChildDurationChangedEvent":
        EntryChildDurationChangedEvent entryChildDurationChangedEvent =
            (EntryChildDurationChangedEvent) event;
        this.childDuration += (entryChildDurationChangedEvent.getDurationDelta());
        break;
      case "EntryChildCountChangedEvent":
        EntryChildCountChangedEvent entryChildCountChangedEvent =
            (EntryChildCountChangedEvent) event;
        this.childCount += entryChildCountChangedEvent.getChildCountDelta();
        break;
      case "EntryPreferredDayConstraintsChangedEvent":
        EntryPreferredDayConstraintsChangedEvent entryPreferredDaysConstraintsChangedEvent =
            (EntryPreferredDayConstraintsChangedEvent) event;
        this.impossibleDaysConstraints =
            entryPreferredDaysConstraintsChangedEvent.getPreferredDayConstraints();
        break;
      default:
        LOGGER.log(
            Level.SEVERE,
            "Event can't be applied to entry "
                + entryId.toString()
                + " ; event type not recognized");
        break;
    }
    eventList.add(event);
  }

  public void applyEvents(List<Event> eventList) {
    for (Event event : eventList) {
      applyEvent(event);
    }
  }

  public Id getEntryId() {
    return entryId;
  }

  public Id getPersonId() {
    return personId;
  }

  public Id getParentEntryId() {
    return parentEntryId;
  }

  public String getTitle() {
    return title;
  }

  public String getNotes() {
    return notes;
  }

  public long getDuration() {
    return duration;
  }

  public OffsetDateTime getStartAtOrAfterDateTime() {
    return startAtOrAfterDateTime;
  }

  public OffsetDateTime getFinishAtOrBeforeDateTime() {
    return finishAtOrBeforeDateTime;
  }

  public long getChildCount() {
    return childCount;
  }

  public long getChildDuration() {
    return childDuration;
  }

  public List<ImpossibleDaysConstraint> getImpossibleDaysConstraints() {
    return impossibleDaysConstraints;
  }

  public boolean isNewTitle(String titleAfter) {
    if (title == null || !title.equals(titleAfter)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNewStartAtOfAfterDateTime(OffsetDateTime startAtOrAfterDateTimeAfter) {
    if ((startAtOrAfterDateTimeAfter == null && startAtOrAfterDateTime != null)
        || (startAtOrAfterDateTimeAfter != null
        && !startAtOrAfterDateTimeAfter.equals(startAtOrAfterDateTime))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNewFinishAtOrBeforeDateTimeAfter(OffsetDateTime finishAtOrBeforeDateTimeAfter) {
    if ((finishAtOrBeforeDateTimeAfter == null && finishAtOrBeforeDateTime != null)
        || (finishAtOrBeforeDateTimeAfter != null
        && !finishAtOrBeforeDateTimeAfter.equals(finishAtOrBeforeDateTime))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNewNotes(String notesAfter) {
    if (notes == null || !notes.equals(notesAfter)) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNewImpossibleDaysConstraint(
      List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter) {
    if (impossibleDaysConstraintsAfter.size() != impossibleDaysConstraints.size()) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isNewDuration(long durationAfter) {
    if (duration != durationAfter) {
      return true;
    } else {
      return false;
    }
  }

  public OffsetDateTime getScheduledStartDateTime() {
    return scheduledStartDateTime;
  }

  public OffsetDateTime getScheduledEndDateTime() {
    return scheduledEndDateTime;
  }
}
