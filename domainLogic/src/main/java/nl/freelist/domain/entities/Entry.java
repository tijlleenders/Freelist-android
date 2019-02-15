package nl.freelist.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class Entry {

  private static final Logger LOGGER = Logger.getLogger(Entry.class.getName());

  private UUID ownerUuid;
  private UUID uuid;
  private UUID parentUuid;
  private String title;
  private String description;
  private int duration;
  private int lastAppliedEventSequenceNumber;
  private List<Event> eventList = new ArrayList<>();

  public Entry(
      UUID ownerUuid, UUID parentUuid, UUID uuid, String title, String description, int duration) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.duration = duration;
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(Level.INFO,
        "Entry " + uuid.toString() + " created with lastAppliedEventSequenceNumber "
            + lastAppliedEventSequenceNumber);
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "EntryCreatedEvent":
        EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
        this.uuid = UUID.fromString(entryCreatedEvent.getEntryId());
        this.ownerUuid = UUID.fromString(entryCreatedEvent.getOwnerUuid());
        this.parentUuid = UUID.fromString(entryCreatedEvent.getParentUuid());
        break;
      case "EntryTitleChangedEvent":
        EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
        this.title = entryTitleChangedEvent.getTitleAfter();
        break;
      case "EntryDescriptionChangedEvent":
        EntryDescriptionChangedEvent entryDescriptionChangedEvent = (EntryDescriptionChangedEvent) event;
        this.description = entryDescriptionChangedEvent.getDescriptionAfter();
        break;
      case "EntryParentChangedEvent":
        EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
        this.parentUuid = UUID.fromString(entryParentChangedEvent.getParentAfter());
        break;
      case "EntryDurationChangedEvent":
        EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
        switch (entryDurationChangedEvent.getUnitOfMeasure()) {
          case "seconds":
            this.duration += (entryDurationChangedEvent.getDurationAfter()
                - entryDurationChangedEvent.getDurationBefore());
            break;
          case "minutes":
            this.duration +=
                (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
                    .getDurationBefore()) * 60;
            break;
          case "hours":
            this.duration +=
                (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
                    .getDurationBefore()) * 3600;
            break;
          case "days":
            this.duration +=
                (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
                    .getDurationBefore()) * 3600 * 24;
            break;
          case "weeks":
            this.duration +=
                (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
                    .getDurationBefore()) * 3600 * 24 * 7;
            break;
          case "years":
            this.duration +=
                (entryDurationChangedEvent.getDurationAfter() - entryDurationChangedEvent
                    .getDurationBefore()) * 3600 * 24 * 265.25;
            break;
          default:
            break;
        }
        break;
      default:
        //Todo: Log or throw?
        break;
    }
    eventList.add(event);
    lastAppliedEventSequenceNumber += 1;

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

  public String getDescription() {
    return description;
  }

  public int getDuration() {
    return duration;
  }


}
