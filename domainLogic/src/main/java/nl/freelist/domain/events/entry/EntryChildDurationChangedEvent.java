package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;

public class EntryChildDurationChangedEvent extends Event {

  private long durationDelta;
  private String eventType = "EntryChildDurationChangedEvent";
  private String originAggregateId;

  private EntryChildDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      String originAggregateId,
      long durationDelta
  ) {
    super(occurredDateTime, entryId);
    this.durationDelta = durationDelta;
    this.originAggregateId = originAggregateId;
  }

  public static EntryChildDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      String originAggregateId,
      long durationDelta
  ) {

    return new EntryChildDurationChangedEvent(
        occurredDateTime,
        entryId,
        originAggregateId,
        durationDelta
    );
  }

  public long getDurationDelta() {
    return durationDelta;
  }

  public String getEventType() {
    return eventType;
  }

  public String getOriginAggregateId() {
    return originAggregateId;
  }
}
