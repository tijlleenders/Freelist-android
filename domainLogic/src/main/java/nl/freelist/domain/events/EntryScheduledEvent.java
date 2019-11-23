package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryScheduledEvent extends Event {

  private String entryUuid;

  private EntryScheduledEvent(
      DateTime occurredDateTime,
      String entryUuid,
      int eventSequenceNumber
  ) {
    super(occurredDateTime, entryUuid, eventSequenceNumber);
    this.entryUuid = entryUuid;
  }

  public static EntryScheduledEvent Create(
      DateTime occurredDateTime,
      String entryUuid,
      int eventSequenceNumber) {
    EntryScheduledEvent entryDescriptionChangedEvent =
        new EntryScheduledEvent(
            occurredDateTime, entryUuid, eventSequenceNumber);
    return entryDescriptionChangedEvent;
  }

}
