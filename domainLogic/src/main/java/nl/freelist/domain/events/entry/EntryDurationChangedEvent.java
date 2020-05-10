package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryDurationChangedEvent extends Event {

  private long durationAfter;
  private String eventType = "EntryDurationChangedEvent";

  private EntryDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      long durationAfter
  ) {
    super(occurredDateTime, entryId);
    this.durationAfter = durationAfter;
  }

  public static EntryDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      long durationAfter
  ) {

    return new EntryDurationChangedEvent(
        occurredDateTime,
        entryId,
        durationAfter
    );
  }

  public long getDurationAfter() {
    return durationAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
