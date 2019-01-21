package nl.freelist.viewModelPerActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.EntryRepository;
import nl.freelist.data.dto.ViewModelEntry;


public class NavigateEntriesViewModel extends AndroidViewModel {

  private String parentUuid;
  private EntryRepository entryRepository;

  public NavigateEntriesViewModel(@NonNull Application application) {
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
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
            () -> entryRepository.getAllViewModelEntriesForParent(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public Observable<List<ViewModelEntry>> getBreadcrumbEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> entryRepository.getBreadcrumbViewModelEntries(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public void updateParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public void deleteAllEntries() {
    // Todo: implement with UseCase
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }
}
