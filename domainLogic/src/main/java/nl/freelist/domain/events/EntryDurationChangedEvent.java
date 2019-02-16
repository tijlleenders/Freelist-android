package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class EntryDurationChangedEvent extends Event {

  private int durationBefore;
  private int durationAfter;
  private String unitOfMeasure;

  private EntryDurationChangedEvent(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      int durationBefore,
      int durationAfter,
      String unitOfMeasure) {
    super(occurredDateTime, entryId, eventSequenceNumber);
    this.durationBefore = durationBefore;
    this.durationAfter = durationAfter;
    this.unitOfMeasure = unitOfMeasure;
  }

  public static EntryDurationChangedEvent Create(
      DateTime occurredDateTime,
      String entryId,
      int eventSequenceNumber,
      int durationBefore,
      int durationAfter,
      String unitOfMeasure) {
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

}
