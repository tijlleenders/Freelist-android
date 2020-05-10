package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryTitleChangedEvent extends Event {

  private String titleAfter;
  private String eventType = "EntryTitleChangedEvent";


  private EntryTitleChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      String titleAfter) {
    super(occurredDateTime, entryId);
    this.titleAfter = titleAfter;
  }

  public static EntryTitleChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      String titleAfter) {

    return new EntryTitleChangedEvent(
        occurredDateTime,
        entryId,
        titleAfter
    );
  }

  public String getTitleAfter() {
    return titleAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
