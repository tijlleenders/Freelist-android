package nl.freelist.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class Entry {

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
  }

  public void applyEvent(Event event) {
    // Todo: ...
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
      default:
        //Todo: Log or throw?
        break;
    }
    eventList.add(event);
    lastAppliedEventSequenceNumber += 1;
  }

  public List<Event> getEventList(int fromEventSequenceNumber) {
    if (eventList.size() > fromEventSequenceNumber) {
      return eventList.subList(fromEventSequenceNumber, eventList.size());
    } else {
      return eventList;
    }
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
