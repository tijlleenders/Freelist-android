package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryChildCountChangedEvent extends Event {

  private long childCountDelta;
  private String eventType = "EntryChildCountChangedEvent";
  private Id originAggregateId;

  private EntryChildCountChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id originAggregateId,
      long childCountDelta
  ) {
    super(occurredDateTime, entryId);
    this.childCountDelta = childCountDelta;
    this.originAggregateId = originAggregateId;
  }

  public static EntryChildCountChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id originAggregateId,
      long childCountDelta
  ) {

    return new EntryChildCountChangedEvent(
        occurredDateTime,
        entryId,
        originAggregateId,
        childCountDelta
    );
  }

  public long getChildCountDelta() {
    return childCountDelta;
  }

  public String getEventType() {
    return eventType;
  }

  public Id getOriginAggregateId() {
    return originAggregateId;
  }
}
