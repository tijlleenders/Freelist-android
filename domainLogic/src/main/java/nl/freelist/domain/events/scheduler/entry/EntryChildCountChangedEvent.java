package nl.freelist.domain.events.scheduler.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryChildCountChangedEvent extends Event {

  private long childCountDelta;
  private Id entryId;
  private String eventType = "EntryChildCountChangedEvent";

  private EntryChildCountChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      long childCountDelta
  ) {
    super(occurredDateTime, personId);
    this.childCountDelta = childCountDelta;
    this.entryId = entryId;
  }

  public static EntryChildCountChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      long childCountDelta
  ) {

    return new EntryChildCountChangedEvent(
        occurredDateTime,
        entryId,
        personId,
        childCountDelta
    );
  }

  public long getChildCountDelta() {
    return childCountDelta;
  }

  public String getEventType() {
    return eventType;
  }

  public Id getEntryId() {
    return entryId;
  }

}
