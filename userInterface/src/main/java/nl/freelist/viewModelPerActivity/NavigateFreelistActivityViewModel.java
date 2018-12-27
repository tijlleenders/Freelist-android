package nl.freelist.viewModelPerActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.EntryRepository;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.GetAllEntriesForParentUseCase;
import nl.freelist.viewModelPerEntity.ViewModelEntry;


public class NavigateFreelistActivityViewModel extends AndroidViewModel {

  private int parentId;
  private EntryRepository entryRepository;

  public NavigateFreelistActivityViewModel(@NonNull Application application) {
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelEntry>> getAllEntries() {
    Observable<List<Entry>> entryList = Observable
        .fromCallable(() -> new GetAllEntriesForParentUseCase(
            entryRepository).execute(parentId))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    Observable<List<ViewModelEntry>> viewModelEntryList = entryList
        .map(entries -> ViewModelEntry.createViewModelEntryListFromEntryList(entries, parentId));
    return viewModelEntryList;
  }

  public void updateParentId(int parentId) {
    this.parentId = parentId;
  }

  public void deleteAllEntries() {
    // Todo: implement with UseCase
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }
}
