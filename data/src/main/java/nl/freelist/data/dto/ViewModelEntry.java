package nl.freelist.data.dto;

import android.util.Log;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;

public final class ViewModelEntry {

  private static final String TAG = "ViewModelEntry dto";

  private final String uuid;
  private final String parentUuid;
  private final String ownerUuid;
  private final String title;
  private final String description;
  private final String durationString;
  private final String childrenDurationString;
  private final int duration;
  private final int years;
  private final int weeks;
  private final int days;
  private final int hours;
  private final int minutes;
  private final int seconds;
  private final int childrenCount;
  private final int childrenDuration;
  private final int lastSavedEventSequenceNumber;

  public ViewModelEntry(
      String ownerUuid,
      String parentUuid,
      String uuid,
      String title,
      String description,
      int duration,
      int childrenCount,
      int childrenDuration,
      int lastSavedEventSequenceNumber) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.childrenCount = childrenCount;
    this.childrenDuration = childrenDuration;
    this.childrenDurationString = DurationHelper.getDurationStringFromInt(childrenDuration);
    this.years = DurationHelper.getYearsIntFrom(duration);
    this.weeks = DurationHelper.getWeeksIntFrom(duration);
    this.days = DurationHelper.getDaysIntFrom(duration);
    this.hours = DurationHelper.getHoursIntFrom(duration);
    this.minutes = DurationHelper.getMinutesIntFrom(duration);
    this.seconds = DurationHelper.getSecondsIntFrom(duration);
    this.durationString = DurationHelper.getDurationStringFromInt(duration);
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

  public String getDescription() {
    return description;
  }

  public String getDurationString() {
    return durationString;
  }

  public int getMinutes() {
    return minutes;
  }

  public int getDuration() {
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

  public int getChildrenCount() {
    return childrenCount;
  }

  public int getChildrenDuration() {
    return childrenDuration;
  }

  public String getChildrenDurationString() {
    return childrenDurationString;
  }

  public int getLastSavedEventSequenceNumber() {
    return lastSavedEventSequenceNumber;
  }

}
