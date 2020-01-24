package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryTitleChangedEvent extends Event {

  private String titleAfter;
  private String eventType = "EntryTitleChangedEvent";


  private EntryTitleChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      String titleAfter) {
    super(occurredDateTime, entryId);
    this.titleAfter = titleAfter;
  }

  public static EntryTitleChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
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
