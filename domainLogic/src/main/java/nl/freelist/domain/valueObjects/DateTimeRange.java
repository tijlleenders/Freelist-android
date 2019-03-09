package nl.freelist.domain.valueObjects;

public class DateTimeRange {

  private DateTime startDateTime;
  private DateTime endDateTime;

  private DateTimeRange(DateTime startDateTime, DateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static DateTimeRange Create(DateTime startDateTime, DateTime endDateTime) {
    return new DateTimeRange(startDateTime, endDateTime);
  }

  public DateTime getStartDateTime() {
    return startDateTime;
  }

  public DateTime getEndDateTime() {
    return endDateTime;
  }
}
