package nl.freelist.data.dto;

import android.util.Log;

public final class ViewModelEvent {

  private static final String TAG = "ViewModelEvent dto";

  private final String occurredDateTime;
  private final String eventMessage;
  private final String entryId;

  public ViewModelEvent(String occurredDateTime, String entryId, String eventMessage) {
    this.occurredDateTime = occurredDateTime;
    this.eventMessage = eventMessage;
    this.entryId = entryId;

    Log.d(TAG, "ViewModelEvent created");
  }

  public String getOccurredDateTime() {
    return occurredDateTime;
  }

  public String getEventMessage() {
    return eventMessage;
  }

  public String getEntryId() {
    return entryId;
  }
}
