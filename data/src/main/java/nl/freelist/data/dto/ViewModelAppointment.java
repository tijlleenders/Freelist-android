package nl.freelist.data.dto;

import android.util.Log;

public final class ViewModelAppointment {

  private static final String TAG = "ViewModelAppointment dto";

  private final String entryId;
  private final String personId;
  private final String title;
  private final int type;
  private final String date;
  private final String time;
  private final String durationString;

  public ViewModelAppointment(String personId, String entryId, String title, int type, String date,
      String time,
      String durationString) {
    this.personId = personId;
    this.entryId = entryId;
    this.title = title;
    this.type = type;
    this.date = date;
    this.time = time;
    this.durationString = durationString;

    Log.d(TAG, "ViewModelAppointment created");
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

  public String getPersonId() {
    return personId;
  }

  public String getEntryId() {
    return entryId;
  }
}
