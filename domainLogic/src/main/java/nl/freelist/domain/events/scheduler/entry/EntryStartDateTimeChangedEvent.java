package nl.freelist.domain.events.scheduler.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryStartDateTimeChangedEvent extends Event {

  private OffsetDateTime startDateTimeAfter;
  private Id entryId;
  private String eventType = "EntryStartDateTimeChangedEvent";

  private EntryStartDateTimeChangedEvent(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, OffsetDateTime startDateTimeAfter) {
    super(occurredDateTime, personId);
    this.entryId = entryId;
    this.startDateTimeAfter = startDateTimeAfter;
  }

  public static EntryStartDateTimeChangedEvent Create(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, OffsetDateTime startDateTimeAfter) {

    return new EntryStartDateTimeChangedEvent(
        occurredDateTime, personId, entryId, startDateTimeAfter);
  }

  public Id getEntryId() {
    return entryId;
  }

  public OffsetDateTime getStartDateTimeAfter() {
    return startDateTimeAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
