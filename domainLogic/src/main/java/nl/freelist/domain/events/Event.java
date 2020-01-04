package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public abstract class Event {

  //Todo: should events get a guid?
  private OffsetDateTime occurredDateTime;
  private String aggregateId;

  protected Event(OffsetDateTime occurredDateTime, String aggregateId) {
    // Create/Validation logic in static Create method of subclasses (easier to test)
    this.occurredDateTime = occurredDateTime;
    this.aggregateId = aggregateId;
  }

  public OffsetDateTime getOccurredDateTime() {
    return occurredDateTime;
  }

  public String getAggregateId() {
    return aggregateId;
  }
}
