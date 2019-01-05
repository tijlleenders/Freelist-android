package nl.freelist.data.dto;

import android.arch.persistence.room.Ignore;

public class DataEntryExtra extends DataEntry {

  private int childrenCount;
  private int childrenDuration;

  @Ignore
  public DataEntryExtra(int id, String title, String description, int duration, int parentId) {
    super(id, title, description, duration, parentId);
  }

  @Ignore
  public DataEntryExtra(String title, String description, int duration, int parentId) {
    super(title, description, duration, parentId);
  }

  public DataEntryExtra(String title, String description, int duration, int parentId,
      int childrenCount, int childrenDuration) {
    super(title, description, duration, parentId);
    this.childrenCount = childrenCount;
    this.childrenDuration = childrenDuration;
  }

  public int getChildrenCount() {
    return childrenCount;
  }

  public void setChildrenCount(int childrenCount) {
    this.childrenCount = childrenCount;
  }

  public int getChildrenDuration() {
    return childrenDuration;
  }

  public void setChildrenDuration(int childrenDuration) {
    this.childrenDuration = childrenDuration;
  }
}
