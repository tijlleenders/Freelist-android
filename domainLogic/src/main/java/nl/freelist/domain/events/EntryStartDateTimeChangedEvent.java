package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryStartDateTimeChangedEvent extends Event {

  private OffsetDateTime startDateTimeAfter;
  private String eventType = "EntryStartDateTimeChangedEvent";

  private EntryStartDateTimeChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      OffsetDateTime startDateTimeAfter
  ) {
    super(occurredDateTime, entryId);
    this.startDateTimeAfter = startDateTimeAfter;
  }

  public static EntryStartDateTimeChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      OffsetDateTime startDateTimeAfter
  ) {

    return new EntryStartDateTimeChangedEvent(
        occurredDateTime,
        entryId,
        startDateTimeAfter
    );
  }

  public OffsetDateTime getStartDateTimeAfter() {
    return startDateTimeAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
 