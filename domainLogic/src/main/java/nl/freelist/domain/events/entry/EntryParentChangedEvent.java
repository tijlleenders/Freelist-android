package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryParentChangedEvent extends Event {

  private Id parentAfter;
  private Id entryId;
  private String eventType = "EntryParentChangedEvent";

  private EntryParentChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      Id parentAfter) {
    super(occurredDateTime, personId);
    this.parentAfter = parentAfter;
    this.entryId = entryId;
  }

  public static EntryParentChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      Id personId,
      Id parentAfter) {

    return new EntryParentChangedEvent(
        occurredDateTime,
        entryId,
        personId,
        parentAfter
    );
  }

  public Id getParentAfter() {
    return parentAfter;
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getEventType() {
    return eventType;
  }
}
