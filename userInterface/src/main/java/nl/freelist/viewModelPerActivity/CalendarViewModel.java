package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelAppointment;

public class CalendarViewModel extends AndroidViewModel {

  private String personId;
  private Repository repository;

  public CalendarViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelAppointment>> getAllCalendarEntries() {
    Observable<List<ViewModelAppointment>> calendarEntryList = Observable
        .fromCallable(
            () -> repository.getAllCalendarEntriesForOwner(personId))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return calendarEntryList;
  }

}
