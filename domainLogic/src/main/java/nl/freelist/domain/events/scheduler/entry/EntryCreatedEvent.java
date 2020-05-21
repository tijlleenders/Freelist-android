package nl.freelist.domain.events.scheduler.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryCreatedEvent extends Event {

  private Id entryId;
  private Id parentEntryId;
  private String eventType = "EntryCreatedEvent"; // Todo: refactor to constants or value object

  private EntryCreatedEvent(OffsetDateTime occurredDateTime, Id personId, Id parentEntryId,
      Id entryId) {
    super(occurredDateTime, personId);
    this.entryId = entryId;
    this.parentEntryId = parentEntryId;
  }

  public static EntryCreatedEvent Create(
      OffsetDateTime occurredDateTime, Id personId, Id parentEntryId, Id entryId) {
    EntryCreatedEvent entryCreatedEvent =
        new EntryCreatedEvent(occurredDateTime, personId, parentEntryId, entryId);
    return entryCreatedEvent;
  }

  public Id getEntryId() {
    return entryId;
  }

  public Id getParentEntryId() {
    return parentEntryId;
  }

  public String getEventType() {
    return eventType;
  }
}
