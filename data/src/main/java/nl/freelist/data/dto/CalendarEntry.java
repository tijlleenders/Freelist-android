package nl.freelist.data.dto;

import android.util.Log;

public class CalendarEntry {

  private static final String TAG = "CalendarEntry";

  private String title;
  private int type;
  private String time;
  private String durationString;

  public CalendarEntry(String title, int type, String time, String durationString) {
    this.title = title;
    this.type = type;
    this.time = time;
    this.durationString = durationString;

    Log.d(TAG, "CalendarEntry created");
  }

  public String getTitle() {
    return title;
  }

  public int getType() {
    return type;
  }

  public String getTime() {
    return time;
  }

  public String getDurationString() {
    return durationString;
  }
}
