package nl.freelist.viewModelPerEntity;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;
import nl.freelist.presentationConstants.ActivityConstants;

public class ViewModelEntry {

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

  public ViewModelEntry(
      int id,
      int parentId,
      String title,
      String description,
      String durationString,
      int duration,
      int years,
      int weeks,
      int days,
      int hours,
      int minutes,
      int seconds,
      int type) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.durationString = durationString;
    this.duration = duration;
    this.years = years;
    this.weeks = weeks;
    this.days = days;
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
    this.type = type;
  }

  public static Entry getEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.id;
    int parentId = viewModelEntry.parentId;
    String title = viewModelEntry.title;
    String description = viewModelEntry.description;
    int duration = viewModelEntry.duration;
    return new Entry(id, parentId, title, description, duration);
  }

  public static ViewModelEntry getViewModelEntryFromEntry(Entry entry) {
    int id = entry.getId();
    int parentId = entry.getParentId();
    String title = entry.getTitle();
    String description = entry.getDescription();
    int duration = max(entry.getDuration(), entry.getChildrenDuration());
    String durationString = DurationHelper.getDurationStringFromInt(duration);
    int years = DurationHelper.getYearsIntFrom(duration);
    int weeks = DurationHelper.getWeeksIntFrom(duration);
    int days = DurationHelper.getDaysIntFrom(duration);
    int hours = DurationHelper.getHoursIntFrom(duration);
    int minutes = DurationHelper.getMinutesIntFrom(duration);
    int seconds = DurationHelper.getSecondsIntFrom(duration);
    int type = ActivityConstants.UNKNOWN_ENTRY_VIEW_TYPE;
    return new ViewModelEntry(
        id,
        parentId,
        title,
        description,
        durationString,
        duration,
        years,
        weeks,
        days,
        hours,
        minutes,
        seconds,
        type);
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public static List<ViewModelEntry> createViewModelEntryListFromEntryList(
      List<Entry> entryList, int parentId) {
    // get list of parents
    List<Integer> parentList = new ArrayList<Integer>();
    for (Entry entry : entryList) {
      parentList.add(entry.getParentId());
    }
    // check whether the id is in list of parents
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    for (Entry entry : entryList) {
      ViewModelEntry viewModelEntry = ViewModelEntry.getViewModelEntryFromEntry(entry);
      if (parentList.contains(entry.getId()) || entry.getId() == parentId) {
        viewModelEntry.setType(ActivityConstants.NODE_ENTRY_VIEW_TYPE);
      } else if (entry.getChildrenCount() >= 1) {
        viewModelEntry.setType(ActivityConstants.MULTIPLE_ENTRY_VIEW_TYPE);
      } else {
        viewModelEntry.setType(ActivityConstants.LEAF_ENTRY_VIEW_TYPE);
      }
      allViewModelEntries.add(viewModelEntry);
    }
    return allViewModelEntries;
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

  public void setDurationString(String durationString) {
    this.durationString = durationString;
  }
}
