package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryCreatedEvent extends Event {

  private String ownerUuid;
  private String parentUuid;
  private int eventSequenceNumber;
  private String eventType = "EntryCreatedEvent";


  private EntryCreatedEvent(OffsetDateTime occurredDateTime, String ownerUuid, String parentUuid,
      String aggregateId, int eventSequenceNumber) {
    super(occurredDateTime, aggregateId);
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static EntryCreatedEvent Create(OffsetDateTime occurredDateTime, String ownerUuid,
      String parentUuid, String aggregateId, int eventSequenceNumber) {
    EntryCreatedEvent entryCreatedEvent = new EntryCreatedEvent(occurredDateTime, ownerUuid,
        parentUuid, aggregateId, eventSequenceNumber);
    return entryCreatedEvent;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  public String getParentUuid() {
    return parentUuid;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }

  public String getEventType() {
    return eventType;
  }
}
