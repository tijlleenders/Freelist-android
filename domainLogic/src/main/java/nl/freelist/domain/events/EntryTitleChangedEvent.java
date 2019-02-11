package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryTitleChangedEvent extends Event {

  private String titleBefore;
  private String titleAfter;

  private EntryTitleChangedEvent(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String titleBefore,
      String titleAfter) {
    super(occurredDateTime, entryId, eventSequenceNumber);
    this.titleBefore = titleBefore;
    this.titleAfter = titleAfter;
  }

  public static EntryTitleChangedEvent Create(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String titleBefore,
      String titleAfter) {
    EntryTitleChangedEvent entryTitleChangedEvent =
        new EntryTitleChangedEvent(
            occurredDateTime, entryId, eventSequenceNumber, titleBefore, titleAfter);
    return entryTitleChangedEvent;
  }

  public String getTitleAfter() {
    return titleAfter;
  }
}
