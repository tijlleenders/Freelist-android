package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryChildDurationChangedEvent extends Event {

  private long durationDelta;
  private Id entryId;
  private String eventType = "EntryChildDurationChangedEvent";

  private EntryChildDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      long durationDelta
  ) {
    super(occurredDateTime, personId);
    this.durationDelta = durationDelta;
    this.entryId = entryId;
  }

  public static EntryChildDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      long durationDelta
  ) {

    return new EntryChildDurationChangedEvent(
        occurredDateTime,
        entryId,
        personId,
        durationDelta
    );
  }

  public long getDurationDelta() {
    return durationDelta;
  }

  public String getEventType() {
    return eventType;
  }

  public Id getEntryId() {
    return entryId;
  }

}
