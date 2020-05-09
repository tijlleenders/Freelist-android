package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;

public class EntryDurationChangedEvent extends Event {

  private long durationAfter;
  private String eventType = "EntryDurationChangedEvent";

  private EntryDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      long durationAfter
  ) {
    super(occurredDateTime, entryId);
    this.durationAfter = durationAfter;
  }

  public static EntryDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
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
