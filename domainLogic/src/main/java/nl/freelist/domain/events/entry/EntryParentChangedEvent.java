package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryParentChangedEvent extends Event {

  private Id parentAfter;
  private String eventType = "EntryParentChangedEvent";

  private EntryParentChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id parentAfter) {
    super(occurredDateTime, entryId);
    this.parentAfter = parentAfter;
  }

  public static EntryParentChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id parentAfter) {

    return new EntryParentChangedEvent(
        occurredDateTime,
        entryId,
        parentAfter
    );
  }

  public Id getParentAfter() {
    return parentAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
