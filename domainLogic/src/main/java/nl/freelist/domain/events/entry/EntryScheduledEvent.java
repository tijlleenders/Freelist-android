package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.aggregates.person.Calendar;
import nl.freelist.domain.events.Event;

public class EntryScheduledEvent extends Event {

  private String resourceUuid;
  private Calendar calendar;
  private String eventType = "EntryScheduledEvent";

  private EntryScheduledEvent(
      OffsetDateTime occurredDateTime,
      String entryUuid,
      String resourceUuid,
      Calendar calendar
  ) {
    super(occurredDateTime, entryUuid);
    this.resourceUuid = resourceUuid;
    this.calendar = calendar;
  }

  public static EntryScheduledEvent Create(
      OffsetDateTime occurredDateTime,
      String entryUuid,
      String resourceUuid,
      Calendar calendar
  ) {
    EntryScheduledEvent entryDescriptionChangedEvent =
        new EntryScheduledEvent(
            occurredDateTime,
            entryUuid,
            resourceUuid,
            calendar
        );
    return entryDescriptionChangedEvent;
  }

  public String getResourceUuid() {
    return resourceUuid;
  }

  public Calendar getCalendar() {
    return calendar;
  }

  public String getEventType() {
    return eventType;
  }
}
