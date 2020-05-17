package nl.freelist.data.dto;

import android.util.Log;
import java.util.List;

public final class ViewModelEntries {

  private static final String TAG = "ViewModelEntries dto";

  private final List<ViewModelEntry> viewModelEntryList;
  private final int lastAppliedSchedulerSequenceNumber;


  public ViewModelEntries(
      List<ViewModelEntry> viewModelEntryList,
      int lastAppliedSchedulerSequenceNumber) {
    this.viewModelEntryList = viewModelEntryList;
    this.lastAppliedSchedulerSequenceNumber = lastAppliedSchedulerSequenceNumber;

    Log.d(
        TAG,
        "initiated ViewModelEntries list with " + viewModelEntryList.size()
            + " entries and last applied scheduler sequence number "
            + lastAppliedSchedulerSequenceNumber);
  }

  public List<ViewModelEntry> getViewModelEntryList() {
    return viewModelEntryList;
  }

  public int getLastAppliedSchedulerSequenceNumber() {
    return lastAppliedSchedulerSequenceNumber;
  }

}
