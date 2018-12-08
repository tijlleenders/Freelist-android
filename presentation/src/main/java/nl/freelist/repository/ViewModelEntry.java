package nl.freelist.repository;

public class ViewModelEntry {

  private int id;
  private int parentId;
  private String parentTitle;
  private String title;
  private String description;
  private String duration;
  private String date;
  private Boolean isCompletedStatus;

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

  public String getParentTitle() {
    return parentTitle;
  }

  public void setParentTitle(String parentTitle) {
    this.parentTitle = parentTitle;
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

  public Boolean getIsCompletedStatus() {
    return isCompletedStatus;
  }

  public void setIsCompletedStatus(Boolean isCompletedStatus) {
    this.isCompletedStatus = isCompletedStatus;
  }
}
