package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryChildCountChangedEvent extends Event {

  private long childCountDelta;
  private String eventType = "EntryChildCountChangedEvent";
  private String originAggregateId;

  private EntryChildCountChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      String originAggregateId,
      long childCountDelta
  ) {
    super(occurredDateTime, entryId);
    this.childCountDelta = childCountDelta;
    this.originAggregateId = originAggregateId;
  }

  public static EntryChildCountChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      String originAggregateId,
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

  public String getOriginAggregateId() {
    return originAggregateId;
  }
}
