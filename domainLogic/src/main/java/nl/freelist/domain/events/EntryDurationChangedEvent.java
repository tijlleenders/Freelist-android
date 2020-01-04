package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryDurationChangedEvent extends Event {

  private int durationBefore;
  private int durationAfter;
  private String unitOfMeasure;
  private int eventSequenceNumber;

  private EntryDurationChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      int durationBefore,
      int durationAfter,
      String unitOfMeasure) {
    super(occurredDateTime, entryId);
    this.durationBefore = durationBefore;
    this.durationAfter = durationAfter;
    this.unitOfMeasure = unitOfMeasure;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static EntryDurationChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      int durationBefore,
      int durationAfter,
      String unitOfMeasure) {
    if (durationAfter == durationBefore) {
      return null;
    }
    EntryDurationChangedEvent entryDurationChangedEvent =
        new EntryDurationChangedEvent(
            occurredDateTime, entryId, eventSequenceNumber, durationBefore, durationAfter,
            unitOfMeasure);
    return entryDurationChangedEvent;
  }

  public int getDurationAfter() {
    return durationAfter;
  }

  public int getDurationBefore() {
    return durationBefore;
  }

  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }
}
