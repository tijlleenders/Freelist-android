package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryParentChangedEvent extends Event {

  private String parentBefore;
  private String parentAfter;

  private EntryParentChangedEvent(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String parentBefore,
      String parentAfter) {
    super(occurredDateTime, entryId, eventSequenceNumber);
    this.parentBefore = parentBefore;
    this.parentAfter = parentAfter;
  }

  public static EntryParentChangedEvent Create(
      DateTime occurredDateTime,
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
}
