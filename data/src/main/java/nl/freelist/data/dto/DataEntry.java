package nl.freelist.data.dto;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "DataEntry")
public class DataEntry {

  @PrimaryKey(autoGenerate = true)
  private int id;
  static final int DEFAULT_PARENT = 0;
  static final String DEFAULT_TYPE = "";

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  private String title;
  private String description;
  private int duration;
  private int parentId;

  @Ignore
  public DataEntry(String title, String description, int duration) {
    this(title, description, duration, DEFAULT_PARENT);
  }

  @Ignore
  public DataEntry() throws Exception {
    throw new Exception("Default constructor for DataEntry not allowed.");
  }

  @Ignore
  public DataEntry(int id, String title, String description, int duration,
      int parentId) {
    this(title, description, duration, parentId);
    this.setId(id);
  }

  public DataEntry(String title, String description, int duration,
      int parentId) {
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.duration = duration;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setParentId(int parent) {
    this.parentId = parent;
  }

  public int getParentId() {
    return parentId;
  }

  public int getId() {
    return id;
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
