package nl.freelist.data.dto;

import android.util.Log;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;

public class ViewModelEntry {

  private static final String TAG = "ViewModelEntry";

  private String uuid;
  private String parentUuid;
  private String ownerUuid;
  private String title;
  private String description;
  private String durationString;
  private int duration;
  private int years;
  private int weeks;
  private int days;
  private int hours;
  private int minutes;
  private int seconds;
  private int childrenCount;
  private int childrenDuration;
  private int lastSavedEventSequenceNumber;
  public int getChildrenCount() {
    return childrenCount;
  }
  public int getChildrenDuration() {
    return childrenDuration;
  }


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

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public int getLastSavedEventSequenceNumber() {
    return lastSavedEventSequenceNumber;
  }

}
