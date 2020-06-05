package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntries;
import nl.freelist.data.dto.ViewModelEntry;

public class NavigateEntriesViewModel extends AndroidViewModel {
  //Todo: should this have multiple observables ie lastEventSequence separate from the list?

  private Repository repository;

  public NavigateEntriesViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public Observable<ViewModelEntries> getViewModelEntries(String personId) {
    //Todo: Cache a copy of viewModelEntries, if UI has not issued a command don't need to re-fetch
    //investigate as viewmodel should not know about the activity> then better place to cache is in activity itself?
    //this would mean making the Observable work so that changes are 'pushed' to activity - not fetched like now
    Observable<ViewModelEntries> viewModelEntries =
        Observable.fromCallable(() -> repository.getViewModelEntries(personId))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io());
    return viewModelEntries;
  }

  @Override
  protected void onCleared() {
    // Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<Boolean> deleteAllEntriesFromRepository() {
    Observable<Boolean> resultObservable =
        Observable.fromCallable(() -> repository.deleteAllEntriesFromRepository());
    return resultObservable;
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }

}
