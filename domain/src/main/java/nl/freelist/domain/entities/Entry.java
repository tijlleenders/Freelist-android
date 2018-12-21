package nl.freelist.domain.entities;

public class Entry {

  private int id;
  private int parentId;
  private String title;
  private String description;
  private int duration;
  private long date;
  private boolean isCompletedStatus;
  private int parent;

  public Entry(
      int id,
      int parentId,
      String parentTitle,
      String title,
      String description,
      int duration,
      long date,
      Boolean isCompletedStatus) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.date = date;
    this.isCompletedStatus = isCompletedStatus;
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

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
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
}
