package nl.freelist.data.dto;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public final class ViewModelEntries {

  private static final String TAG = "ViewModelEntries dto";

  private final List<ViewModelEntry> viewModelEntryList;
  private final int lastAppliedSchedulerSequenceNumber;
  private final String personId;


  public ViewModelEntries(
      List<ViewModelEntry> viewModelEntryList,
      String personId,
      int lastAppliedSchedulerSequenceNumber) {
    this.viewModelEntryList = viewModelEntryList;
    this.personId = personId;
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

  public String getPersonId() {
    return personId;
  }

  public int getLastAppliedSchedulerSequenceNumber() {
    return lastAppliedSchedulerSequenceNumber;
  }

  public List<ViewModelEntry> getViewModelEntryList(
      String parentSet) { //Todo: inefficient but not yet a problem so ok for now
    List<ViewModelEntry> result = new ArrayList(viewModelEntryList);
    result.removeIf(viewModelEntry -> !viewModelEntry.getParentEntryId().equals(parentSet));
    return result;
  }

  public ViewModelEntry getEntry(String entryId) {
    for (ViewModelEntry entry : viewModelEntryList) {
      if (entry.getEntryId().equals(entryId)) {
        return entry;
      }
    }
    return null;
  }
}
