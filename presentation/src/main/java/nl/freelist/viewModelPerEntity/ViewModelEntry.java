package nl.freelist.viewModelPerEntity;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;

public class ViewModelEntry {

  private int id;
  private int parentId;
  private String parentTitle;
  private String title;
  private String description;
  private String duration;
  private int parent;

  public ViewModelEntry(
      int id,
      int parentId,
      String parentTitle,
      String title,
      String description,
      String duration) {
    this.id = id;
    this.parentId = parentId;
    this.parentTitle = parentTitle;
    this.title = title;
    this.description = description;
    this.duration = duration;
  }

  public static Entry getEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.id;
    int parentId = viewModelEntry.parentId;
    String parentTitle = viewModelEntry.parentTitle;
    String title = viewModelEntry.title;
    String description = viewModelEntry.description;
    int duration = DurationHelper.getDurationIntFromString(viewModelEntry.duration);
    return new Entry(id, parentId, parentTitle, title, description, duration);
  }

  public static ViewModelEntry getViewModelEntryFromEntry(Entry entry) {
    int id = entry.getId();
    int parentId = entry.getParentId();
    String parentTitle = entry.getTitle(); //Todo: fix
    String title = entry.getTitle();
    String description = entry.getDescription();
    String duration = "todo"; //Todo:fix entry.getDuration();
    return new ViewModelEntry(id, parentId, parentTitle, title, description, duration);
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

