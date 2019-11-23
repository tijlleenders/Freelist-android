package nl.freelist.data.dto;

import android.util.Log;

public class ViewModelEvent {

  private static final String TAG = "ViewModelEvent dto";

  private String occurredDateTime;
  private String entryId;

  public ViewModelEvent(String occurredDateTime, String entryId) {
    this.occurredDateTime = occurredDateTime;
    this.entryId = entryId;

    Log.d(TAG, "ViewModelEvent created");
  }

  public String getEntryId() {
    return entryId;
  }

  public String getOccurredDateTime() {
    return occurredDateTime;
  }
}
