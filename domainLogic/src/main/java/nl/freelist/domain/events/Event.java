package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public abstract class Event {

  //Todo: should events get a guid?
  private OffsetDateTime occurredDateTime;

  protected Event(OffsetDateTime occurredDateTime) {
    // Create/Validation logic in static Create method of subclasses (easier to test)
    this.occurredDateTime = occurredDateTime;
  }

  public OffsetDateTime getOccurredDateTime() {
    return occurredDateTime;
  }
}
