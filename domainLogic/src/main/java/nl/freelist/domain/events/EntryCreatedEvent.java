package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryCreatedEvent extends Event {

  private String ownerUuid;
  private String parentUuid;
  private String eventType = "EntryCreatedEvent";


  private EntryCreatedEvent(
      OffsetDateTime occurredDateTime,
      String ownerUuid,
      String parentUuid,
      String aggregateId
  ) {
    super(occurredDateTime, aggregateId);
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
  }

  public static EntryCreatedEvent Create(
      OffsetDateTime occurredDateTime,
      String ownerUuid,
      String parentUuid,
      String aggregateId
  ) {
    EntryCreatedEvent entryCreatedEvent = new EntryCreatedEvent(
        occurredDateTime,
        ownerUuid,
        parentUuid,
        aggregateId
    );
    return entryCreatedEvent;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  public String getParentUuid() {
    return parentUuid;
  }

  public String getEventType() {
    return eventType;
  }
}
