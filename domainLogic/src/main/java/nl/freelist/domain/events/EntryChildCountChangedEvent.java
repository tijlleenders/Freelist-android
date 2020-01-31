package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryChildCountChangedEvent extends Event {

  private long childCountDelta;
  private String eventType = "EntryChildCountChangedEvent";

  private EntryChildCountChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      long childCountDelta
  ) {
    super(occurredDateTime, entryId);
    this.childCountDelta = childCountDelta;
  }

  public static EntryChildCountChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      long childCountDelta
  ) {

    return new EntryChildCountChangedEvent(
        occurredDateTime,
        entryId,
        childCountDelta
    );
  }

  public long getChildCountDelta() {
    return childCountDelta;
  }

  public String getEventType() {
    return eventType;
  }
}
