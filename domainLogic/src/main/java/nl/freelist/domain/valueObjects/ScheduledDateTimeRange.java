package nl.freelist.domain.valueObjects;

import java.time.OffsetDateTime;

public class ScheduledDateTimeRange { //Todo: delete

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;
  private String entryId;
  private boolean isDone;

  private ScheduledDateTimeRange(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static ScheduledDateTimeRange Create(OffsetDateTime startDateTime,
      OffsetDateTime endDateTime) {
    return new ScheduledDateTimeRange(startDateTime, endDateTime);
  }

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }
}
