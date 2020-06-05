package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntries;

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

  public Observable<ViewModelEntries> getViewModelEntries() {
    Observable<ViewModelEntries> viewModelEntries =
        Observable.fromCallable(() -> repository.getViewModelEntriesForCalendar(personId))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io());
    return viewModelEntries;
  }

}
