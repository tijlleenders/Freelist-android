package nl.freelist.domain.events;

import java.time.OffsetDateTime;
import nl.freelist.domain.entities.Calendar;

public class EntryScheduledEvent extends Event {

  private String entryUuid;
  private String resourceUuid;
  private int resourceEventSequenceNumber;
  private int entryEventSequenceNumber;
  private Calendar calendar;

  private EntryScheduledEvent(
      OffsetDateTime occurredDateTime,
      String entryUuid,
      String resourceUuid,
      int resourceEventSequenceNumber,
      int entryEventSequenceNumber,
      Calendar calendar
  ) {
    super(occurredDateTime);
    this.entryUuid = entryUuid;
    this.resourceUuid = resourceUuid;
    this.resourceEventSequenceNumber = resourceEventSequenceNumber;
    this.entryEventSequenceNumber = entryEventSequenceNumber;
    this.calendar = calendar;
  }

  public static EntryScheduledEvent Create(
      OffsetDateTime occurredDateTime,
      String entryUuid,
      String resourceUuid,
      int resourceEventSequenceNumber,
      int entryEventSequenceNumber,
      Calendar calendar
  ) {
    EntryScheduledEvent entryDescriptionChangedEvent =
        new EntryScheduledEvent(
            occurredDateTime, entryUuid, resourceUuid, resourceEventSequenceNumber,
            entryEventSequenceNumber, calendar);
    return entryDescriptionChangedEvent;
  }

  public String getEntryUuid() {
    return entryUuid;
  }

  public String getResourceUuid() {
    return resourceUuid;
  }

  public int getResourceEventSequenceNumber() {
    return resourceEventSequenceNumber;
  }

  public int getEntryEventSequenceNumber() {
    return entryEventSequenceNumber;
  }

  public Calendar getCalendar() {
    return calendar;
  }
}
