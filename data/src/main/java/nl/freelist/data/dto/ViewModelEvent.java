package nl.freelist.data.dto;

import android.util.Log;

public class ViewModelEvent {

  private static final String TAG = "ViewModelEvent dto";

  private String occurredDateTime;
  private String entryId;
  private String eventMessage;

  public ViewModelEvent(String occurredDateTime, String entryId, String eventMessage) {
    this.occurredDateTime = occurredDateTime;
    this.entryId = entryId;
    this.eventMessage = eventMessage;

    Log.d(TAG, "ViewModelEvent created");
  }

  public String getEntryId() {
    return entryId;
  }

  public String getOccurredDateTime() {
    return occurredDateTime;
  }

  public String getEventMessage() {
    return eventMessage;
  }
}
