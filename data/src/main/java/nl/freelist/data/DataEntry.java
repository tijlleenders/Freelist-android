package nl.freelist.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "DataEntry")
public class DataEntry {

  @PrimaryKey(autoGenerate = true)
  private int id;
  static final int DEFAULT_PARENT = 0;
  final private String title;
  final private String description;
  final private int duration;
  private long date;
  private boolean isCompletedStatus;
  private int parent;

  @Ignore
  public DataEntry(String title, String description, int duration, long date,
      boolean isCompletedStatus) {
    this(title, description, duration, date, isCompletedStatus, DEFAULT_PARENT);
  }

  @Ignore
  public DataEntry(int id, String title, String description, int duration, long date,
      boolean isCompletedStatus, int parent) {
    this(title, description, duration, date, isCompletedStatus, parent);
    this.setId(id);
  }

  public DataEntry(String title, String description, int duration, long date,
      boolean isCompletedStatus, int parent) {
    this.parent = parent;
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.date = date;
    this.isCompletedStatus = isCompletedStatus;
  }


  public void setId(int id) {
    this.id = id;
  }

  public void setParent(int parent) {
    this.parent = parent;
  }

  public int getParent() {
    return parent;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public void setCompletedStatus(boolean completedStatus) {
    isCompletedStatus = completedStatus;
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

  public long getDate() {
    return date;
  }

  public String getFormattedDate() {
    Date date = new Date(this.date);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d", Locale.US);
    return formatter.format(date);
  }

  public boolean getIsCompletedStatus() {
    return isCompletedStatus;
  }
}
