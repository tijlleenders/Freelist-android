package nl.freelist.data.dto;

import android.util.Log;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.valueObjects.TimeSlot;

public class ViewModelCalendarOption {

  private static final String TAG = "ViewModelCalendarOption dto";

  private int numberOfProblems;
  private int numberOfReschedules;
  private String personId;
  private String entryId;
  private TimeSlot scheduledTimeSlot;
  private int resourceLastAppliedEventSequenceNumber;
  private int entryLastAppliedEventSequenceNumber;


  public ViewModelCalendarOption(
      int numberOfProblems,
      int numberOfReschedules,
      String personId,
      String entryId,
      TimeSlot scheduledTimeSlot,
      int resourceLastAppliedEventSequenceNumber,
      int entryLastAppliedEventSequenceNumber) {
    this.numberOfProblems = numberOfProblems;
    this.numberOfReschedules = numberOfReschedules;
    this.personId = personId;
    this.entryId = entryId;
    this.scheduledTimeSlot = scheduledTimeSlot;
    this.resourceLastAppliedEventSequenceNumber = resourceLastAppliedEventSequenceNumber;
    this.entryLastAppliedEventSequenceNumber = entryLastAppliedEventSequenceNumber;
    ;

    Log.d(TAG,
        "ViewModelCalendarOption created");
  }

  public int getResourceLastAppliedEventSequenceNumber() {
    return resourceLastAppliedEventSequenceNumber;
  }

  public String getEntryId() {
    return entryId;
  }

  public TimeSlot getScheduledTimeSlot() {
    return scheduledTimeSlot;
  }

  public String getScheduledDate() {
    return scheduledTimeSlot.getStartDateTime().toLocalDate().toString();
  }

  public String getScheduledTime() {
    return scheduledTimeSlot.getStartDateTime().toLocalTime().toString();
  }

  public String getDurationString() {
    return TimeHelper.getDurationStringFrom(scheduledTimeSlot.getDuration());
  }

  public String getPersonId() {
    return personId;
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

}
