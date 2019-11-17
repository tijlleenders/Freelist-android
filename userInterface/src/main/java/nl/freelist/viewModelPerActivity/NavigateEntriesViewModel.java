package nl.freelist.viewModelPerActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;

// Todo: rename (parent)Uuid to (parent)entryId

public class NavigateEntriesViewModel extends AndroidViewModel {

  private String parentUuid;
  private Repository repository;

  public NavigateEntriesViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public void setParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public String getParentUuid() {
    return parentUuid;
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelEntry>> getAllChildrenEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> repository.getAllViewModelEntriesForParent(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public Observable<List<ViewModelEntry>> getBreadcrumbEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> repository.getBreadcrumbViewModelEntries(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public void updateParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
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
}
