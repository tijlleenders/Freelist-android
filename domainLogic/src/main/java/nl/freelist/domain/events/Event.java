package nl.freelist.domain.events;

import java.time.OffsetDateTime;
import nl.freelist.domain.valueObjects.Id;

public abstract class Event {

  //Todo: should events get a guid?
  private OffsetDateTime occurredDateTime;
  private Id aggregateId;
  private Id eventId;

  protected Event(OffsetDateTime occurredDateTime, Id aggregateId) {
    // Create/Validation logic in static Create method of subclasses (easier to test)
    this.occurredDateTime = occurredDateTime;
    this.aggregateId = aggregateId;
    eventId = Id.Create();
  }

  public OffsetDateTime getOccurredDateTime() {
    return occurredDateTime;
  }

  public Id getAggregateId() {
    return aggregateId;
  }

  public Id getEventId() {
    return eventId;
  }
}
