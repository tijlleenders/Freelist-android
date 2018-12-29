package nl.freelist.domain.entities;

public class Entry {

  private int id;
  private int parentId;
  private String title;
  private String description;
  private int duration;
  private int childrenCount;
  private int childrenDuration;

  public Entry(
      int id, int parentId, String title, String description, int duration) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.duration = duration;
  }

  public Entry(
      int id, int parentId, String title, String description, int duration,
      int childrenCount, int childrenDuration) {
    this.id = id;
    this.parentId = parentId;
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

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }
}
