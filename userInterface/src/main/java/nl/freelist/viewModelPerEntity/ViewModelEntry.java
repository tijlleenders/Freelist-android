package nl.freelist.viewModelPerEntity;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;
import nl.freelist.presentationConstants.ActivityConstants;

public class ViewModelEntry {

  private int id;
  private int parentId;
  private String title;
  private String description;
  private String duration;
  private int type;

  public ViewModelEntry(
      int id,
      int parentId,
      String title,
      String description,
      String duration,
      int type) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.type = type;
  }

  public static Entry getEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.id;
    int parentId = viewModelEntry.parentId;
    String title = viewModelEntry.title;
    String description = viewModelEntry.description;
    int duration = DurationHelper.getDurationIntFromString(viewModelEntry.duration);
    return new Entry(id, parentId, title, description, duration);
  }

  public static ViewModelEntry getViewModelEntryFromEntry(Entry entry) {
    int id = entry.getId();
    int parentId = entry.getParentId();
    String title = entry.getTitle();
    String description = entry.getDescription();
    String duration =
        DurationHelper.getDurationStringFromInt(
            max(entry.getDuration(), entry.getChildrenDuration()));
    int type = ActivityConstants.UNKNOWN_ENTRY_VIEW_TYPE;
    return new ViewModelEntry(id, parentId, title, description, duration, type);
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

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

}
