package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryDescriptionChangedEvent extends Event {

  private String descriptionBefore;
  private String descriptionAfter;
  private String entryId;
  private int eventSequenceNumber;

  private EntryDescriptionChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      String descriptionBefore,
      String descriptionAfter) {
    super(occurredDateTime);
    this.descriptionBefore = descriptionBefore;
    this.descriptionAfter = descriptionAfter;
    this.entryId = entryId;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static EntryDescriptionChangedEvent Create(
      OffsetDateTime occurredDateTime,
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

  public String getDescriptionBefore() {
    return descriptionBefore;
  }

  public String getEntryUuid() {
    return entryId;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
