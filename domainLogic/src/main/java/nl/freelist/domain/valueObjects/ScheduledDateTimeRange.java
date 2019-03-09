package nl.freelist.domain.valueObjects;

public class ScheduledDateTimeRange {

  private DateTime startDateTime;
  private DateTime endDateTime;
  private String entryId;
  private boolean isDone;

  private ScheduledDateTimeRange(DateTime startDateTime, DateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static ScheduledDateTimeRange Create(DateTime startDateTime, DateTime endDateTime) {
    return new ScheduledDateTimeRange(startDateTime, endDateTime);
  }

  public DateTime getStartDateTime() {
    return startDateTime;
  }

  public DateTime getEndDateTime() {
    return endDateTime;
  }
}
