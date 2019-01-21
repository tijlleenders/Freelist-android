package nl.freelist.domain.entities;

import java.util.UUID;

public class Entry {

  private UUID ownerUuid;
  private UUID uuid;
  private UUID parentUuid;
  private String title;
  private String description;
  private int duration;
  private int childrenCount;
  private int childrenDuration;

  public Entry(
      UUID ownerUuid, UUID parentUuid, UUID uuid, String title, String description, int duration) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.duration = duration;
  }

  public Entry(
      UUID ownerUuid, UUID parentUuid, UUID uuid, String title, String description, int duration,
      int childrenCount, int childrenDuration) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.duration = duration;
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

  public UUID getUuid() {
    return uuid;
  }

  public UUID getOwnerUuid() {
    return ownerUuid;
  }

  public UUID getParentUuid() {
    return parentUuid;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getDuration() {
    return duration;
  }

}
