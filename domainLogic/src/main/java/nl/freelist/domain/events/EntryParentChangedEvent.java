package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryParentChangedEvent extends Event {

  private String parentBefore;
  private String parentAfter;
  private String entryId;
  private int eventSequenceNumber;

  private EntryParentChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String parentBefore,
      String parentAfter) {
    super(occurredDateTime);
    this.parentBefore = parentBefore;
    this.parentAfter = parentAfter;
    this.entryId = entryId;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static EntryParentChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String parentBefore,
      String parentAfter) {
    EntryParentChangedEvent entryParentChangedEvent =
        new EntryParentChangedEvent(
            occurredDateTime, entryId, eventSequenceNumber, parentBefore, parentAfter);
    return entryParentChangedEvent;
  }

  public String getParentBefore() {
    return parentBefore;
  }

  public String getParentAfter() {
    return parentAfter;
  }

  public String getEntryUuid() {
    return entryId;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
