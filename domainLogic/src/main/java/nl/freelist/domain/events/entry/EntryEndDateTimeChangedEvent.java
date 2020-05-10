package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryEndDateTimeChangedEvent extends Event {

  private OffsetDateTime endDateTimeAfter;
  private String eventType = "EntryEndDateTimeChangedEvent";

  private EntryEndDateTimeChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      OffsetDateTime endDateTimeAfter
  ) {
    super(occurredDateTime, entryId);
    this.endDateTimeAfter = endDateTimeAfter;
  }

  public static EntryEndDateTimeChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      OffsetDateTime endDateTimeAfter
  ) {

    return new EntryEndDateTimeChangedEvent(
        occurredDateTime,
        entryId,
        endDateTimeAfter
    );
  }

  public OffsetDateTime getEndDateTimeAfter() {
    return endDateTimeAfter;
  }

  public String getEventType() {
    return eventType;
  }
}