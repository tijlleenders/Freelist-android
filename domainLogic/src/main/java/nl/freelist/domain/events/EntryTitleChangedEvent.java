package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryTitleChangedEvent extends Event {

  private String titleBefore;
  private String titleAfter;
  private int eventSequenceNumber;

  private EntryTitleChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String titleBefore,
      String titleAfter) {
    super(occurredDateTime, entryId);
    this.titleBefore = titleBefore;
    this.titleAfter = titleAfter;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static EntryTitleChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String titleBefore,
      String titleAfter) {
    if (titleAfter.equals(titleBefore)) {
      return null;
    }
    EntryTitleChangedEvent entryTitleChangedEvent =
        new EntryTitleChangedEvent(
            occurredDateTime, entryId, eventSequenceNumber, titleBefore, titleAfter);
    return entryTitleChangedEvent;
  }

  public String getTitleAfter() {
    return titleAfter;
  }

  public String getTitleBefore() {
    return titleBefore;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
