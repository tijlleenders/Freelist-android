package nl.freelist.data.dto;

import android.util.Log;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;


public class ViewModelEntry {

  private static final String TAG = "ViewModelEntry";

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

  private int id;
  private int parentId;
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
  private int type;
  private int childrenCount;
  private int childrenDuration;

  public int getChildrenCount() {
    return childrenCount;
  }

  public int getChildrenDuration() {
    return childrenDuration;
  }
  public ViewModelEntry(
      int id,
      int parentId,
      String title,
      String description,
      int duration,
      int type,
      int childrenCount,
      int childrenDuration) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.type = type;
    this.childrenCount = childrenCount;
    this.childrenDuration = childrenDuration;
    this.years = DurationHelper.getYearsIntFrom(duration);
    this.weeks = DurationHelper.getWeeksIntFrom(duration);
    this.days = DurationHelper.getDaysIntFrom(duration);
    this.hours = DurationHelper.getHoursIntFrom(duration);
    this.minutes = DurationHelper.getMinutesIntFrom(duration);
    this.seconds = DurationHelper.getSecondsIntFrom(duration);
    this.durationString = DurationHelper.getDurationStringFromInt(duration);

    Log.d(TAG,
        "ViewModelEntry " + title + " (id:" + Integer.toString(id) + ")" + " parentId:" + parentId);
  }

  public static Entry getEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.id;
    int parentId = viewModelEntry.parentId;
    String title = viewModelEntry.title;
    String description = viewModelEntry.description;
    int duration = viewModelEntry.duration;
    return new Entry(id, parentId, title, description, duration);
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getParentId() {
    return parentId;
  }

  public void setParentId(int parentId) {
    this.parentId = parentId;
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

}
