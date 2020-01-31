package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryChildDurationChangedEvent extends Event {

  private long durationDelta;
  private String eventType = "EntryChildDurationChangedEvent";

  private EntryChildDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      long durationDelta
  ) {
    super(occurredDateTime, entryId);
    this.durationDelta = durationDelta;
  }

  public static EntryChildDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      long durationDelta
  ) {

    return new EntryChildDurationChangedEvent(
        occurredDateTime,
        entryId,
        durationDelta
    );
  }

  public long getDurationDelta() {
    return durationDelta;
  }

  public String getEventType() {
    return eventType;
  }
}
