package nl.freelist.domain.aggregates.plan;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.entry.EntryChildCountChangedEvent;
import nl.freelist.domain.events.entry.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryCreatedEvent;
import nl.freelist.domain.events.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.entry.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryTitleChangedEvent;
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
  private OffsetDateTime startDateTime;
  private long duration = 0;
  private OffsetDateTime endDateTime;
  private List<ImpossibleDaysConstraint> impossibleDaysConstraints = new ArrayList<>();
  private String notes = "";
  private long childCount; // are applied within same transaction that add descendants
  private long
      childDuration; // are applied within same transaction that changes descendant duration
  private List<Constraint> startAndDueConstraints = new ArrayList<>();
  private List<Constraint> timeBudgetConstraints = new ArrayList<>();
  private List<Constraint> repeatConstraints = new ArrayList<>();
  private List<Event> eventList = new ArrayList<>();

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
        // Do nothing
        break;
      case "EntryStartDateTimeChangedEvent":
        EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent =
            (EntryStartDateTimeChangedEvent) event;
          this.startDateTime = entryStartDateTimeChangedEvent.getStartDateTimeAfter();
        break;
      case "EntryEndDateTimeChangedEvent":
        EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent =
            (EntryEndDateTimeChangedEvent) event;
          this.endDateTime = entryEndDateTimeChangedEvent.getEndDateTimeAfter();
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

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
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
}
