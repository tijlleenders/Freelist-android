package nl.freelist.repository;

public class ViewModelEntry {

  private int id;
  private int parentId;
  private String parentTitle;
  private String title;
  private String description;
  private String duration;
  private String date;
  private boolean isCompletedStatus;

  public ViewModelEntry(int id, int parentId, String parentTitle, String title,
      String description, String duration, String date, boolean isCompletedStatus) {
    this.id = id;
    this.parentId = parentId;
    this.parentTitle = parentTitle;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.date = date;
    this.isCompletedStatus = isCompletedStatus;
  }
}
