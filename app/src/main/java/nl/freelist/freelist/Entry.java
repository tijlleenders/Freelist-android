package nl.freelist.freelist;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "entry")
class Entry {

  @PrimaryKey(autoGenerate = true)
  private int id;
  final private String title;
  final private String description;
  final private int duration;
  private long date;
  private boolean isCompletedStatus;

  public Entry(String title, String description, int duration, long date,
      boolean isCompletedStatus) {
    this.title = title;
    this.description = description;
    this.duration = duration;
    this.date = date;
    this.isCompletedStatus = isCompletedStatus;
  }

  public void setId(int id) {
    this.id = id;
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

  int getDuration() {
    return duration;
  }

  String getFormattedDuration() {
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

  long getDate() {
    return date;
  }

  String getFormattedDate() {
    Date date = new Date(this.date);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d", Locale.US);
    return formatter.format(date);
  }

  boolean getIsCompletedStatus() {
    return isCompletedStatus;
  }
}
