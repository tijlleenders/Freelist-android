package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public abstract class Event {

  private DateTime occurredDateTime;
  private String entryId; //Todo: replace with uuidString VO
  private int eventSequenceNumber;

  protected Event(DateTime occurredDateTime, String entryId, int eventSequenceNumber) {
    // Create/Validation logic in static Create method of subclasses (easier to test)
    this.occurredDateTime = occurredDateTime;
    this.entryId = entryId;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public DateTime getOccurredDateTime() {
    return occurredDateTime;
  }

  public String getEntryId() {
    return entryId;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
