package nl.freelist.viewModelPerEntity;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.DateHelper;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;

public class ViewModelEntry {

  private int id;
  private int parentId;
  private String parentTitle;
  private String title;
  private String description;
  private String duration;
  private String date;
  private boolean isCompletedStatus;
  private int parent;

  public ViewModelEntry(
      int id,
      int parentId,
      String parentTitle,
      String title,
      String description,
      String duration,
      String date,
      Boolean isCompletedStatus) {
    this.id = id;
    this.parentId = parentId;
    this.parentTitle = parentTitle;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.date = date;
    this.isCompletedStatus = isCompletedStatus;
  }

  public static Entry getEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.id;
    int parentId = viewModelEntry.parentId;
    String parentTitle = viewModelEntry.parentTitle;
    String title = viewModelEntry.title;
    String description = viewModelEntry.description;
    int duration = DurationHelper.getDurationIntFromString(viewModelEntry.duration);
    long date = DateHelper.getLongFromString(viewModelEntry.date);
    Boolean isCompletedStatus = viewModelEntry.isCompletedStatus;
    return new Entry(id, parentId, parentTitle, title, description, duration, date,
        isCompletedStatus);
  }

  public static ViewModelEntry getViewModelEntryFromEntry(Entry entry) {
    int id = entry.getId();
    int parentId = entry.getParentId();
    String parentTitle = entry.getTitle(); //Todo: fix
    String title = entry.getTitle();
    String description = entry.getDescription();
    String duration = "todo"; //Todo:fix entry.getDuration();
    String date = "2010-01-01"; //Todo: fix entry.getDate();
    Boolean isCompletedStatus = entry.isCompletedStatus();
    return new ViewModelEntry(id, parentId, parentTitle, title, description, duration, date,
        isCompletedStatus);
  }

  public static List<ViewModelEntry> createViewModelEntryListFromEntryList(List<Entry> entryList) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    for (Entry entry : entryList) {
      ViewModelEntry viewModelEntry = ViewModelEntry.getViewModelEntryFromEntry(entry);
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

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public boolean isCompletedStatus() {
    return isCompletedStatus;
  }

  public void setCompletedStatus(boolean completedStatus) {
    isCompletedStatus = completedStatus;
  }

  public int getParent() {
    return parent;
  }

  public void setParent(int parent) {
    this.parent = parent;
  }

  public String getParentTitle() {
    return parentTitle;
  }
}

