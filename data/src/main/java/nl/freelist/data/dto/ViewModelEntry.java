package nl.freelist.data.dto;

import android.util.Log;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public final class ViewModelEntry {

  private static final String TAG = "ViewModelEntry dto";

  private final String entryId;
  private final String parentEntryId;
  private final String personId;
  private final String title;
  private final OffsetDateTime startDateTime;
  private final String durationString;
  private final OffsetDateTime endDateTime;
  private final List<ImpossibleDaysConstraint> impossibleDaysConstraints;
  private final String notes;
  private final String childrenDurationString;
  private final long duration;
  private final int years;
  private final int weeks;
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;
  private long childrenCount;
  private long childrenDuration;
  private final int lastAppliedSchedulerSequenceNumber;

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  public ViewModelEntry(
      Id personId,
      Id parentEntryId,
      Id entryId,
      String title,
      OffsetDateTime startDateTime,
      long duration,
      OffsetDateTime endDateTime,
      List<ImpossibleDaysConstraint> impossibleDaysConstraints,
      String notes,
      long childrenCount,
      long childrenDuration,
      int lastAppliedSchedulerSequenceNumber) {
    this.personId = personId.toString();
    this.parentEntryId = parentEntryId.toString();
    this.entryId = entryId.toString();
    this.title = title;
    this.startDateTime = startDateTime;
    this.duration = duration;
    this.endDateTime = endDateTime;
    if (impossibleDaysConstraints == null) {
      this.impossibleDaysConstraints = new ArrayList<>();
    } else {
      this.impossibleDaysConstraints = impossibleDaysConstraints;
    }
    this.notes = notes;
    this.childrenCount = childrenCount;
    this.childrenDuration = childrenDuration;
    this.childrenDurationString = TimeHelper.getDurationStringFrom(childrenDuration);
    this.years = TimeHelper.getWholeYearsFrom(duration);
    this.weeks = TimeHelper.getStandaloneWeeksFrom(duration);
    this.days = TimeHelper.getStandaloneDaysFrom(duration);
    this.hours = TimeHelper.getStandaloneHoursFrom(duration);
    this.minutes = TimeHelper.getStandaloneMinutesFrom(duration);
    this.seconds = TimeHelper.getStandaloneSecondsFrom(duration);
    this.durationString = TimeHelper.getDurationStringFrom(duration);
    this.lastAppliedSchedulerSequenceNumber = lastAppliedSchedulerSequenceNumber;

    Log.d(
        TAG,
        "ViewModelEntry " + title + " (uuid:" + entryId + ")" + " parentUuid:" + parentEntryId);
  }

  public String getEntryId() {
    return entryId;
  }

  public String getParentEntryId() {
    return parentEntryId;
  }

  public String getPersonId() {
    return personId;
  }

  public int getType() {
    if (childrenCount > 0) {
      return Constants.STACK_ENTRY_VIEW_TYPE;
    } else {
      return Constants.SINGLE_ENTRY_VIEW_TYPE;
    }
  }

  public String getTitle() {
    return title;
  }

  public String getNotes() {
    return notes;
  }

  public List<ImpossibleDaysConstraint> getImpossibleDaysConstraints() {
    return impossibleDaysConstraints;
  }

  public String getDurationString() {
    return durationString;
  }

  public int getMinutes() {
    return minutes;
  }

  public long getDuration() {
    return duration;
  }

  public int getYears() {
    return years;
  }

  public int getWeeks() {
    return weeks;
  }

  public int getDays() {
    return days;
  }

  public int getHours() {
    return hours;
  }

  public int getSeconds() {
    return seconds;
  }

  public long getChildrenCount() {
    return childrenCount;
  }

  public long getChildrenDuration() {
    return childrenDuration;
  }

  public String getChildrenDurationString() {
    return childrenDurationString;
  }

  public int getLastAppliedSchedulerSequenceNumber() {
    return lastAppliedSchedulerSequenceNumber;
  }

}
