package nl.freelist.data;

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

  public String getFormattedDuration() {
    if (duration > 0) {
      int hours = (duration / 3600);
      int minutes = (duration % 3600) / 60;
      int seconds = duration % 60;
      StringBuilder formattedDuration = new StringBuilder();
      if (hours > 0) {
        formattedDuration.append(Integer.toString(hours)).append("h");
      }
      if (minutes > 0) {
        formattedDuration.append(Integer.toString(minutes)).append("m");
      }
      if (seconds > 0) {
        formattedDuration.append(Integer.toString(seconds)).append("s");
      }
      return formattedDuration.toString();
    } else {
      return "...";
    }
  }
}
