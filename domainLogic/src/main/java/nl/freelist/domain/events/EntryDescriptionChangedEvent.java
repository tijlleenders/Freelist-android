package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryDescriptionChangedEvent extends Event {

  private String descriptionBefore;
  private String descriptionAfter;

  private EntryDescriptionChangedEvent(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String descriptionBefore,
      String descriptionAfter) {
    super(occurredDateTime, entryId, eventSequenceNumber);
    this.descriptionBefore = descriptionBefore;
    this.descriptionAfter = descriptionAfter;
  }

  public static EntryDescriptionChangedEvent Create(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String descriptionBefore,
      String descriptionAfter) {
    EntryDescriptionChangedEvent entryDescriptionChangedEvent =
        new EntryDescriptionChangedEvent(
            occurredDateTime, entryId, eventSequenceNumber, descriptionBefore, descriptionAfter);
    return entryDescriptionChangedEvent;
  }

  public String getDescriptionAfter() {
    return descriptionAfter;
  }
}
