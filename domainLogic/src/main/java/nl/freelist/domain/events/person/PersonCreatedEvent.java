package nl.freelist.domain.events.person;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;

public class PersonCreatedEvent extends Event {

  private String eventType = "PersonCreatedEvent";

  private PersonCreatedEvent(
      OffsetDateTime occurredDateTime,
      String personId) {
    super(occurredDateTime, personId);
  }

  public static PersonCreatedEvent Create(OffsetDateTime occurredDateTime, String personID) {
    PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent(occurredDateTime, personID);
    return personCreatedEvent;
  }

  public String getEventType() {
    return eventType;
  }
}
