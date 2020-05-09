package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;

public class EntryParentChangedEvent extends Event {

  private String parentAfter;
  private String eventType = "EntryParentChangedEvent";

  private EntryParentChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      String parentAfter) {
    super(occurredDateTime, entryId);
    this.parentAfter = parentAfter;
  }

  public static EntryParentChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      String parentAfter) {

    return new EntryParentChangedEvent(
        occurredDateTime,
        entryId,
        parentAfter
    );
  }

  public String getParentAfter() {
    return parentAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
