package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryChildDurationChangedEvent extends Event {

  private long durationDelta;
  private String eventType = "EntryChildDurationChangedEvent";
  private Id originAggregateId;

  private EntryChildDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id originAggregateId,
      long durationDelta
  ) {
    super(occurredDateTime, entryId);
    this.durationDelta = durationDelta;
    this.originAggregateId = originAggregateId;
  }

  public static EntryChildDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id originAggregateId,
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

  public Id getOriginAggregateId() {
    return originAggregateId;
  }
}
