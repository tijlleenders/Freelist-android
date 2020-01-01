package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.CalendarEntry;

public class CalendarViewModel extends AndroidViewModel {

  private String ownerUuid;
  private Repository repository;

  public CalendarViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public void setOwnerUuid(String ownerUuid) {
    this.ownerUuid = ownerUuid;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<CalendarEntry>> getAllCalendarEntries() {
    Observable<List<CalendarEntry>> calendarEntryList = Observable
        .fromCallable(
            () -> repository.getAllCalendarEntriesForOwner(UUID.fromString(ownerUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return calendarEntryList;
  }

  public void updateOwnerUuid(String ownerUuid) {
    this.ownerUuid = ownerUuid;
  }

}
