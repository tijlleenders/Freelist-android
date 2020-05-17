package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntries;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.valueObjects.Id;

public class NavigateEntriesViewModel extends AndroidViewModel {

  private String parentId;
  private String personId;
  private Repository repository;

  public NavigateEntriesViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public Observable<ViewModelEntries> getViewModelEntries() {
    Observable<ViewModelEntries> viewModelEntries = Observable
        .fromCallable(
            () -> repository.getViewModelEntriesForParent(parentId, personId))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntries;
  }


  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelEntry>> getBreadcrumbEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> repository.getBreadcrumbViewModelEntries(Id.fromString(parentId)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public String getParentId() {
    return parentId;
  }

  public Observable<Boolean> deleteAllEntriesFromRepository() {
    Observable<Boolean> resultObservable = Observable
        .fromCallable(
            () -> repository.deleteAllEntriesFromRepository());
    return resultObservable;
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  } // not in constructor as this changes while navigating and ViewModel does not have to be re-created

  public void setPersonId(String personId) {
    this.personId = personId;
  } // not in constructor as this changes while navigating and ViewModel does not have to be re-created


}
