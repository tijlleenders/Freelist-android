package nl.freelist.domain.events.person.calendar;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;

public class CalendarCreatedEvent extends Event {

  private String eventType = "CalendarCreatedEvent";

  private CalendarCreatedEvent(
      OffsetDateTime occurredDateTime,
      String calendarId
  ) {
    super(occurredDateTime, calendarId);
  }

  public static CalendarCreatedEvent Create(
      OffsetDateTime occurredDateTime,
      String calendarId
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
