package nl.freelist.domain.valueObjects;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot { // start- and endDateTime are inclusive

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;

  private TimeSlot(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static TimeSlot Create(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    if (startDateTime.isAfter(endDateTime)
        || startDateTime.isEqual((endDateTime))
        || startDateTime == null) {
      exit(-9);
    }

    return new TimeSlot(startDateTime, endDateTime);
  }

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  public int getDuration() {
    long duration;
    duration = startDateTime.until(endDateTime, ChronoUnit.SECONDS);
    return (int) duration;
  }

  public TimeSlot plus(TimeSlot b) {
    OffsetDateTime start;
    OffsetDateTime end;

    if (this.startDateTime.isBefore(b.startDateTime)) {
      start = this.startDateTime;
    } else {
      start = b.startDateTime;
    }
    if (this.endDateTime.isAfter(b.endDateTime)) {
      end = this.endDateTime;
    } else {
      end = b.endDateTime;
    }

    return TimeSlot.Create(start, end);
  }


  @Override
  public boolean equals(Object o) {
    TimeSlot b = (TimeSlot) o;
    if (this.startDateTime.equals(b.startDateTime) && this.endDateTime.equals(b.endDateTime)) {
      return true;
    } else {
      return false;
    }

  }

  public Boolean isWithin(TimeSlot b) {
    if (this.startDateTime.isAfter(b.startDateTime)
        && this.endDateTime.isBefore(b.endDateTime)) {
      return true;
    }
    return false;
  }

  public List<TimeSlot> minus(TimeSlot b) {
    if (this.startDateTime.isAfter(b.startDateTime)
        || this.endDateTime
        .isBefore(b.endDateTime)) { //refuse to substract a TimeSlot that doesn't fit within
      exit(-9);
    }

    OffsetDateTime start;
    OffsetDateTime end;
    List<TimeSlot> result = new ArrayList<>();
    TimeSlot firstTimeSlot;
    TimeSlot secondTimeSlot;

    if (b.isWithin(this)) {
      firstTimeSlot = TimeSlot.Create(this.startDateTime, b.startDateTime);
      secondTimeSlot = TimeSlot.Create(b.endDateTime, this.endDateTime);
      result.add(firstTimeSlot);
      result.add(secondTimeSlot);
      return result;
    }

    if (this.startDateTime.equals(b.startDateTime)) {
      firstTimeSlot = TimeSlot.Create(b.endDateTime, this.endDateTime);
      result.add(firstTimeSlot);
      return result;
    }

    if (this.endDateTime.equals(b.endDateTime)) {
      firstTimeSlot = TimeSlot.Create(this.startDateTime, b.startDateTime);
      result.add(firstTimeSlot);
      return result;
    }

    return null;
  }
}
