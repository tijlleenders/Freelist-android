package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryCreatedEvent extends Event {

  private String ownerUuid;
  private String parentUuid;

  private EntryCreatedEvent(DateTime occurredDateTime, String ownerUuid, String parentUuid,
      String aggregateId, int eventSequenceNumber) {
    super(occurredDateTime, aggregateId, eventSequenceNumber);
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
  }

  public static EntryCreatedEvent Create(DateTime occurredDateTime, String ownerUuid,
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

}
