package nl.freelist.data.dto;

import android.util.Log;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.entities.Calendar;
import nl.freelist.domain.valueObjects.DateTimeRange;

public class ViewModelCalendarOption {

  private static final String TAG = "ViewModelCalendarOption dto";

  private int numberOfProblems;
  private int numberOfReschedules;
  private String resourceUuid;
  private String entryUuid;
  private DateTimeRange scheduledDateTimeRange;
  private int resourceLastAppliedEventSequenceNumber;
  private int entryLastAppliedEventSequenceNumber;
  private Calendar calendar;

  public ViewModelCalendarOption(
      int numberOfProblems,
      int numberOfReschedules,
      String resourceUuid,
      String entryUuid,
      DateTimeRange scheduledDateTimeRange,
      int resourceLastAppliedEventSequenceNumber,
      int entryLastAppliedEventSequenceNumber,
      Calendar calendar) {
    this.numberOfProblems = numberOfProblems;
    this.numberOfReschedules = numberOfReschedules;
    this.resourceUuid = resourceUuid;
    this.entryUuid = entryUuid;
    this.scheduledDateTimeRange = scheduledDateTimeRange;
    this.resourceLastAppliedEventSequenceNumber = resourceLastAppliedEventSequenceNumber;
    this.entryLastAppliedEventSequenceNumber = entryLastAppliedEventSequenceNumber;
    this.calendar = calendar;

    Log.d(TAG,
        "ViewModelCalendarOption created");
  }

  public int getResourceLastAppliedEventSequenceNumber() {
    return resourceLastAppliedEventSequenceNumber;
  }

  public String getEntryUuid() {
    return entryUuid;
  }

  public DateTimeRange getScheduledDateTimeRange() {
    return scheduledDateTimeRange;
  }

  public String getScheduledDate() {
    return scheduledDateTimeRange.getStartDateTime().toLocalDate().toString();
  }

  public String getScheduledTime() {
    return scheduledDateTimeRange.getStartDateTime().toLocalTime().toString();
  }

  public String getDurationString() {
    return TimeHelper.getDurationStringFrom(scheduledDateTimeRange.getDuration());
  }

  public String getResourceUuid() {
    return resourceUuid;
  }

  public int getType() {
    if (numberOfProblems == 0) {
      if (numberOfReschedules == 0) {
        return Constants.PRIO_NO_PROBLEM_TYPE;
      } else {
        return Constants.PRIO_ONLY_RESCHEDULES_TYPE;
      }
    } else {
      return Constants.PRIO_PROBLEM_TYPE;
    }
  }

  public String getNumberOfProblems() {
    if (numberOfProblems == 0) {
      return "";
    } else {
      return numberOfProblems + " problems";
    }
  }

  public String getNumberOfReschedules() {
    if (numberOfReschedules == 0) {
      return "";
    } else {
      return numberOfReschedules + " reschedules";
    }
  }

  public int getEntryLastAppliedEventSequenceNumber() {
    return entryLastAppliedEventSequenceNumber;
  }

  public Calendar getCalendar() {
    return calendar;
  }
}
