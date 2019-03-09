package nl.freelist.domain.valueObjects;

public class FreeDateTimeRange {

  private DateTime startDateTime;
  private DateTime endDateTime;

  private FreeDateTimeRange(DateTime startDateTime, DateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static FreeDateTimeRange Create(DateTime startDateTime, DateTime endDateTime) {
    return new FreeDateTimeRange(startDateTime, endDateTime);
  }

  public DateTime getStartDateTime() {
    return startDateTime;
  }

  public DateTime getEndDateTime() {
    return endDateTime;
  }
}
