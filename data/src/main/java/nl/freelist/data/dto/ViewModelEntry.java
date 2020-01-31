package nl.freelist.data.dto;

import android.util.Log;
import java.time.OffsetDateTime;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;

public final class ViewModelEntry {

  private static final String TAG = "ViewModelEntry dto";

  private final String uuid;
  private final String parentUuid;
  private final String ownerUuid;
  private final String title;
  private final OffsetDateTime startDateTime;
  private final String durationString;
  private final OffsetDateTime endDateTime;
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
  private final int lastSavedEventSequenceNumber;

  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  public OffsetDateTime getEndDateTime() {
    return endDateTime;
  }

  public ViewModelEntry(
      String ownerUuid,
      String parentUuid,
      String uuid,
      String title,
      OffsetDateTime startDateTime,
      long duration,
      OffsetDateTime endDateTime,
      String notes,
      long childrenCount,
      long childrenDuration,
      int lastSavedEventSequenceNumber) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.startDateTime = startDateTime;
    this.duration = duration;
    this.endDateTime = endDateTime;
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
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;

    Log.d(TAG,
        "ViewModelEntry " + title + " (uuid:" + uuid + ")" + " parentUuid:" + parentUuid);
  }

  public String getUuid() {
    return uuid;
  }

  public String getParentUuid() {
    return parentUuid;
  }

  public String getOwnerUuid() {
    return ownerUuid;
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

  public int getLastSavedEventSequenceNumber() {
    return lastSavedEventSequenceNumber;
  }

}
