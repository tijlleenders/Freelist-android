package nl.freelist.data.dto;

import android.util.Log;

public final class CalendarEntry {

  private static final String TAG = "CalendarEntry dto";

  private final String entryUuid;
  private final String title;
  private final int type;
  private final String date;
  private final String time;
  private final String durationString;

  public CalendarEntry(String entryUuid, String title, int type, String date, String time,
      String durationString) {
    this.entryUuid = entryUuid;
    this.title = title;
    this.type = type;
    this.date = date;
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

  public String getDate() {
    return date;
  }

  public String getEntryUuid() {
    return entryUuid;
  }
}
