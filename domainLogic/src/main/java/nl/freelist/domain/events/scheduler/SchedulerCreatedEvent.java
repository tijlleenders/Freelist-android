package nl.freelist.domain.events.scheduler;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class SchedulerCreatedEvent extends Event {

  private String eventType = "SchedulerCreatedEvent";

  private SchedulerCreatedEvent(
      OffsetDateTime occurredDateTime,
      Id personId
  ) {
    super(occurredDateTime, personId); //Scheduler has no identity of its own 1:1 to person/resource
  }

  public static SchedulerCreatedEvent Create(
      OffsetDateTime occurredDateTime, Id personId
  ) {
    SchedulerCreatedEvent SchedulerCreatedEvent = new SchedulerCreatedEvent(
        occurredDateTime,
        personId
    );
    return SchedulerCreatedEvent;
  }

  public String getEventType() {
    return eventType;
  }

  public Id getPersonId() {
    return super.getAggregateId();
  }

}
