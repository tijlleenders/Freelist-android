package nl.freelist.domain.events.person;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class PersonCreatedEvent extends Event {

  private String eventType = "PersonCreatedEvent";

  private PersonCreatedEvent(
      OffsetDateTime occurredDateTime,
      Id personId) {
    super(occurredDateTime, personId);
  }

  public static PersonCreatedEvent Create(OffsetDateTime occurredDateTime, Id personId) {
    PersonCreatedEvent personCreatedEvent = new PersonCreatedEvent(occurredDateTime, personId);
    return personCreatedEvent;
  }

  public String getEventType() {
    return eventType;
  }
}
