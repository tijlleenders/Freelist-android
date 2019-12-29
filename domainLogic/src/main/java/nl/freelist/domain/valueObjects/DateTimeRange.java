package nl.freelist.domain.valueObjects;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeRange { //start- and endDateTime are inclusive

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;

  private DateTimeRange(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static DateTimeRange Create(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual((endDateTime))) {
      exit(-9);
    }
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
