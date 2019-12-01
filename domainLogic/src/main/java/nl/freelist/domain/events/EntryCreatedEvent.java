package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryCreatedEvent extends Event {

  private String ownerUuid;
  private String parentUuid;
  private String entryId;
  private int eventSequenceNumber;


  private EntryCreatedEvent(OffsetDateTime occurredDateTime, String ownerUuid, String parentUuid,
      String aggregateId, int eventSequenceNumber) {
    super(occurredDateTime);
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.entryId = aggregateId;
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

  public String getEntryUuid() {
    return entryId;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
