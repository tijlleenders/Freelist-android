package nl.freelist.domain.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.EntryChildCountChangedEvent;
import nl.freelist.domain.events.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class Entry {

  private static final Logger LOGGER = Logger.getLogger(Entry.class.getName());
  // Todo: When adding new event:
  // Add to SaveEntryCommand
  // Add to this.applyEvent(Event event)
  // Add to EventDatabaseHelper.jsonOf(Event event)
  // Add to EventDatabaseHelper.getEventsFor(String uuid)
  // Possibly add to EventDatabaseHelper.getAdditionalQueriesForEvent(Event event, Entry entry,
  //      int eventSequenceNumber)
  // Add to ViewModelEntry dto
  // Add to EventDatabaseHelper.getViewModelEntryFrom(Entry entry)
  // Possibly List<ViewModelEvent> in Repository.getAllEventsForID(String uuid) (for UI history)

  private UUID ownerUuid;
  private UUID uuid;
  private UUID parentUuid;
  private String title = "";
  private OffsetDateTime startDateTime;
  private long duration = 0;
  private OffsetDateTime endDateTime;
  private List<DtrConstraint> preferredDaysConstraints = new ArrayList<>();
  private String notes = "";
  private long childCount; // are applied within same transaction that add descendants
  private long
      childDuration; // are applied within same transaction that changes descendant duration
  private int lastAppliedEventSequenceNumber;
  private List<DtrConstraint> startAndDueConstraints = new ArrayList<>();
  private List<DtrConstraint> timeBudgetConstraints = new ArrayList<>();
  private List<DtrConstraint> repeatConstraints = new ArrayList<>();
  private List<Event> eventList = new ArrayList<>();

  public Entry(
      // Todo: make private and expose via public static method Entry.Create so validation can be
      // included
      ) {
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(
        Level.INFO,
        "Entry created with lastAppliedEventSequenceNumber " + lastAppliedEventSequenceNumber);
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?

    if (event == null) {
      return;
    }
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "EntryCreatedEvent":
        LOGGER.log(Level.INFO, "EntryCreatedEvent applied");
        if (lastAppliedEventSequenceNumber != -1) {
          LOGGER.log(Level.WARNING, "EntryCreatedEvent applied to entry that already exists!");
          break;
        }
        EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
        this.uuid = UUID.fromString(entryCreatedEvent.getAggregateId());
        this.ownerUuid = UUID.fromString(entryCreatedEvent.getOwnerUuid());
        this.parentUuid = UUID.fromString(entryCreatedEvent.getParentUuid());
        eventList.add(event);
        lastAppliedEventSequenceNumber += 1;
        break;
      case "EntryTitleChangedEvent":
        LOGGER.log(Level.INFO, "EntryTitleChangedEvent applied");
        EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
        if (this.title == null || !this.title.equals(entryTitleChangedEvent.getTitleAfter())) {
          this.title = entryTitleChangedEvent.getTitleAfter();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryNotesChangedEvent":
        LOGGER.log(Level.INFO, "EntryNotesChangedEvent applied");
        EntryNotesChangedEvent entryNotesChangedEvent = (EntryNotesChangedEvent) event;
        if (this.notes == null || !this.notes.equals(entryNotesChangedEvent.getNotesAfter())) {
          this.notes = entryNotesChangedEvent.getNotesAfter();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryParentChangedEvent":
        LOGGER.log(Level.INFO, "EntryParentChangedEvent applied");
        EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
        if (this.parentUuid == null
            || !this.parentUuid.equals(entryParentChangedEvent.getParentAfter())) {
          this.parentUuid = UUID.fromString(entryParentChangedEvent.getParentAfter());
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryDurationChangedEvent":
        LOGGER.log(Level.INFO, "EntryDurationChangedEvent applied");
        EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
        if (this.duration != entryDurationChangedEvent.getDurationAfter()) {
          this.duration = entryDurationChangedEvent.getDurationAfter();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryScheduledEvent":
        LOGGER.log(Level.INFO, "EntryScheduledEvent applied");
        // Do nothing
        eventList.add(event);
        lastAppliedEventSequenceNumber += 1;
        break;
      case "EntryStartDateTimeChangedEvent":
        LOGGER.log(Level.INFO, "EntryStartDateTimeChangedEvent applied");
        EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent =
            (EntryStartDateTimeChangedEvent) event;
        if ((entryStartDateTimeChangedEvent.getStartDateTimeAfter() == null
                && this.startDateTime != null)
            || (entryStartDateTimeChangedEvent.getStartDateTimeAfter() != null
                && !entryStartDateTimeChangedEvent
                    .getStartDateTimeAfter()
                    .equals(this.startDateTime))) {
          this.startDateTime = entryStartDateTimeChangedEvent.getStartDateTimeAfter();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryEndDateTimeChangedEvent":
        LOGGER.log(Level.INFO, "EntryEndDateTimeChangedEvent applied");
        EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent =
            (EntryEndDateTimeChangedEvent) event;
        if ((entryEndDateTimeChangedEvent.getEndDateTimeAfter() == null && this.endDateTime != null)
            || (entryEndDateTimeChangedEvent.getEndDateTimeAfter() != null
                && !entryEndDateTimeChangedEvent.getEndDateTimeAfter().equals(this.endDateTime))) {
          this.endDateTime = entryEndDateTimeChangedEvent.getEndDateTimeAfter();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryChildDurationChangedEvent":
        LOGGER.log(Level.INFO, "EntryChildDurationChangedEvent applied");
        EntryChildDurationChangedEvent entryChildDurationChangedEvent =
            (EntryChildDurationChangedEvent) event;
        if (entryChildDurationChangedEvent.getDurationDelta() != 0
            && !uuid.toString()
                .equals(
                    entryChildDurationChangedEvent.getOriginAggregateId()) // avoid circular effect
        ) {
          this.childDuration += (entryChildDurationChangedEvent.getDurationDelta());
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryChildCountChangedEvent":
        LOGGER.log(Level.INFO, "EntryChildCountChangedEvent applied");
        EntryChildCountChangedEvent entryChildCountChangedEvent =
            (EntryChildCountChangedEvent) event;
        if (entryChildCountChangedEvent.getChildCountDelta() != 0
            && !uuid.toString()
                .equals(entryChildCountChangedEvent.getOriginAggregateId()) // avoid circular effect
        ) {
          this.childCount += entryChildCountChangedEvent.getChildCountDelta();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      case "EntryPreferredDayConstraintsChangedEvent":
        LOGGER.log(Level.INFO, "EntryPreferredDayConstraintsChangedEvent applied");
        EntryPreferredDayConstraintsChangedEvent entryPreferredDaysConstraintsChangedEvent =
            (EntryPreferredDayConstraintsChangedEvent) event;
        if (entryPreferredDaysConstraintsChangedEvent.getPreferredDayConstraints().size()
            != this.preferredDaysConstraints.size()) {
          this.preferredDaysConstraints =
              entryPreferredDaysConstraintsChangedEvent.getPreferredDayConstraints();
          eventList.add(event);
          lastAppliedEventSequenceNumber += 1;
        }
        break;
      default:
        LOGGER.log(
            Level.WARNING,
            "Event can't be applied to entry " + uuid.toString() + " ; event type not recognized");
        break;
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

  public UUID getUuid() {
    return uuid;
  }

  public UUID getOwnerUuid() {
    return ownerUuid;
  }

  public UUID getParentUuid() {
    return parentUuid;
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

  public List<Event> getEventList() {
    return eventList;
  }

  public List<DtrConstraint> getPreferredDaysConstraints() {
    return preferredDaysConstraints;
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
