package nl.freelist.data.dto;

import java.util.UUID;

public class DataEntryExtra extends DataEntry {

  private int childrenCount;
  private int childrenDuration;

  public DataEntryExtra(UUID ownerUuid, UUID parentUuid, UUID uuid, String title,
      String description, int duration, int childrenCount, int childrenDuration) {
    super(ownerUuid, parentUuid, uuid, title, description, duration);
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
