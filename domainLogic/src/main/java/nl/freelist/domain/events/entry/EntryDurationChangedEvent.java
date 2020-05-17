package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryDurationChangedEvent extends Event {

  private Id entryId;
  private long durationAfter;
  private String eventType = "EntryDurationChangedEvent";

  private EntryDurationChangedEvent(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, long durationAfter) {
    super(occurredDateTime, personId);
    this.durationAfter = durationAfter;
    this.entryId = entryId;
  }

  public static EntryDurationChangedEvent Create(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, long durationAfter) {

    return new EntryDurationChangedEvent(occurredDateTime, personId, entryId, durationAfter);
  }

  public Id getEntryId() {
    return entryId;
  }

  public long getDurationAfter() {
    return durationAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
