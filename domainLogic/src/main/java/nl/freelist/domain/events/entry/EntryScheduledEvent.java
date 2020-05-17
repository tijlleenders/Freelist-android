package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryScheduledEvent extends Event {

  private Id entryId;
  private String eventType = "EntryScheduledEvent";

  private EntryScheduledEvent(
      OffsetDateTime occurredDateTime, Id entryId, Id personId) {
    super(occurredDateTime, personId);
    this.entryId = entryId;
  }

  public static EntryScheduledEvent Create(OffsetDateTime occurredDateTime, Id entryId,
      Id personId) {
    EntryScheduledEvent entryScheduledEvent =
        new EntryScheduledEvent(occurredDateTime, entryId, personId);
    return entryScheduledEvent;
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getEventType() {
    return eventType;
  }
}
