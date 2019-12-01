package nl.freelist.domain.valueObjects;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeRange {

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;

  private DateTimeRange(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static DateTimeRange Create(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    return new DateTimeRange(startDateTime, endDateTime);
  }

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  public int getDuration() {
    long duration;
    duration
        = startDateTime.until(endDateTime,
        ChronoUnit.SECONDS);
    return (int) duration;
  }
}
