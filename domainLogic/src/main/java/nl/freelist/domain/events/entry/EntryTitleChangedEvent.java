package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryTitleChangedEvent extends Event {

  private Id entryId;
  private String titleAfter;
  private String eventType = "EntryTitleChangedEvent";

  private EntryTitleChangedEvent(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, String titleAfter) {
    super(occurredDateTime, personId);
    this.titleAfter = titleAfter;
    this.entryId = entryId;
  }

  public static EntryTitleChangedEvent Create(
      OffsetDateTime occurredDateTime, Id personId, Id entryId, String titleAfter) {
    return new EntryTitleChangedEvent(occurredDateTime, personId, entryId, titleAfter);
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getTitleAfter() {
    return titleAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
