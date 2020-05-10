package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryCreatedEvent extends Event {

  private Id ownerUuid;
  private Id parentUuid;
  private String eventType = "EntryCreatedEvent"; // Todo: refactor to constants or value object


  private EntryCreatedEvent(
      OffsetDateTime occurredDateTime,
      Id ownerUuid,
      Id parentUuid,
      Id aggregateId
  ) {
    super(occurredDateTime, aggregateId);
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
  }

  public static EntryCreatedEvent Create(
      OffsetDateTime occurredDateTime,
      Id ownerUuid,
      Id parentUuid,
      Id aggregateId
  ) {
    EntryCreatedEvent entryCreatedEvent = new EntryCreatedEvent(
        occurredDateTime,
        ownerUuid,
        parentUuid,
        aggregateId
    );
    return entryCreatedEvent;
  }

  public Id getOwnerUuid() {
    return ownerUuid;
  }

  public Id getParentUuid() {
    return parentUuid;
  }

  public String getEventType() {
    return eventType;
  }
}
