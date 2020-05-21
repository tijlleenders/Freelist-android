package nl.freelist.domain.valueObjects;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Constants;

public class TimeSlot implements Comparable { // start- and endDateTime are inclusive

  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;
  private Id entryId;

  private TimeSlot(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }

  public static TimeSlot Create(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
    if (startDateTime.isAfter(endDateTime)
        || startDateTime.isEqual((endDateTime))
        || startDateTime == null) {
      System.out.println("TimeSlote Create: conditions not fulfilled.");
      exit(-9);
    }
    return new TimeSlot(startDateTime, endDateTime);
  }

  public static TimeSlot Create(
      OffsetDateTime startDateTime, OffsetDateTime endDateTime, Id entryId) {
    TimeSlot timeSlot = TimeSlot.Create(startDateTime, endDateTime);
    timeSlot.entryId = entryId;
    return timeSlot;
  }

  public static TimeSlot CreateFreeForever() {
    TimeSlot timeSlot = new TimeSlot(Constants.START_OF_TIME, Constants.END_OF_TIME);
    return timeSlot;
  }

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  public long getDuration() {
    long duration;
    duration = startDateTime.until(endDateTime, ChronoUnit.SECONDS);
    if (duration > Constants.SECONDS_FROM_BEGINNING_TO_END_OF_TIME) {
      System.out.printf("Timeslot duration higher than constant seconds to end of time!");
      exit(-9);
    }
    return duration;
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

  public Id getEntryId() {
    return entryId;
  }

  public List<TimeSlot> minus(TimeSlot b) {
    if (this.startDateTime.isAfter(b.startDateTime) || this.endDateTime.isBefore(b.endDateTime)) {
      System.out.println("TimeSlot: trying to substract a TimeSlot that doesn't fit within.");
      exit(-9);
    }

    List<TimeSlot> result = new ArrayList<>();
    TimeSlot firstTimeSlot;
    TimeSlot secondTimeSlot;

    if (this.startDateTime.equals(b.startDateTime) && this.endDateTime.equals(b.endDateTime)) {
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

    if (this.startDateTime.isBefore(b.startDateTime) || this.endDateTime.isAfter(b.endDateTime)) {
      firstTimeSlot = TimeSlot.Create(this.startDateTime, b.startDateTime);
      secondTimeSlot = TimeSlot.Create(b.endDateTime, this.endDateTime);
      result.add(firstTimeSlot);
      result.add(secondTimeSlot);
      return result;
    }

    return null;
  }

  public Long getKey() {
    return ((endDateTime.toEpochSecond() - startDateTime.toEpochSecond()) << 32)
        //duration on left 32 bits
        | (startDateTime.toEpochSecond() - Constants.START_OF_TIME
        .toEpochSecond()); //startDateTime on right 32 bits
  }

  @Override
  public int compareTo(Object o) {
    if (o == null) {
      throw new NullPointerException();
    }
    TimeSlot otherTimeSlot = (TimeSlot) o;

    if (this.equals(otherTimeSlot)) {
      return 0;
    }
    if (this.getKey() < otherTimeSlot.getKey()) {
      return 1;
    } else {
      return -1;
    }
  }
}
