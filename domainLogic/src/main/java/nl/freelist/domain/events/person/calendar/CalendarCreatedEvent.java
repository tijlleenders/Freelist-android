package nl.freelist.domain.events.person.calendar;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class CalendarCreatedEvent extends Event {

  private String eventType = "CalendarCreatedEvent";

  private CalendarCreatedEvent(
      OffsetDateTime occurredDateTime,
      Id calendarId
  ) {
    super(occurredDateTime, calendarId);
  }

  public static CalendarCreatedEvent Create(
      OffsetDateTime occurredDateTime,
      Id calendarId
  ) {
    CalendarCreatedEvent CalendarCreatedEvent = new CalendarCreatedEvent(
        occurredDateTime,
        calendarId
    );
    return CalendarCreatedEvent;
  }

  public String getEventType() {
    return eventType;
  }
}
