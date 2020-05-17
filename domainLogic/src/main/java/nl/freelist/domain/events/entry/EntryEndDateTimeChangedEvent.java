package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryEndDateTimeChangedEvent extends Event {

  private Id entryId;
  private OffsetDateTime endDateTimeAfter;
  private String eventType = "EntryEndDateTimeChangedEvent";

  private EntryEndDateTimeChangedEvent(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, OffsetDateTime endDateTimeAfter) {
    super(occurredDateTime, personId);
    this.endDateTimeAfter = endDateTimeAfter;
    this.entryId = entryId;
  }

  public static EntryEndDateTimeChangedEvent Create(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, OffsetDateTime endDateTimeAfter) {

    return new EntryEndDateTimeChangedEvent(occurredDateTime, personId, entryId, endDateTimeAfter);
  }

  public Id getEntryId() {
    return entryId;
  }

  public OffsetDateTime getEndDateTimeAfter() {
    return endDateTimeAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
