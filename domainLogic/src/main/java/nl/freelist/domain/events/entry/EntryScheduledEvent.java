package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryScheduledEvent extends Event {

  private Id personId;
  private String eventType = "EntryScheduledEvent";

  private EntryScheduledEvent(
      OffsetDateTime occurredDateTime, Id entryId, Id personId) {
    super(occurredDateTime, entryId);
    this.personId = personId;
  }

  public static EntryScheduledEvent Create(OffsetDateTime occurredDateTime, Id entryId,
      Id personId) {
    EntryScheduledEvent entryScheduledEvent =
        new EntryScheduledEvent(occurredDateTime, entryId, personId);
    return entryScheduledEvent;
  }

  public Id getPersonId() {
    return personId;
  }

  public String getEventType() {
    return eventType;
  }
}
