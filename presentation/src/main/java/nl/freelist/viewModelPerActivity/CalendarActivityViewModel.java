package nl.freelist.viewModelPerActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.EntryRepository;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.GetAllEntriesUseCase;
import nl.freelist.viewModelPerEntity.ViewModelEntry;


public class CalendarActivityViewModel extends AndroidViewModel {

  private EntryRepository entryRepository;

  public CalendarActivityViewModel(@NonNull Application application) {
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelEntry>> getAllEntries() {
    Observable<List<Entry>> entryList = Observable.fromCallable(() -> new GetAllEntriesUseCase(
        entryRepository).execute())
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    Observable<List<ViewModelEntry>> viewModelEntryList = entryList
        .map(entries -> ViewModelEntry.createViewModelEntryListFromEntryList(entries));
    return viewModelEntryList;
  }

  public void deleteAllEntries() {
    // Todo: implement with UseCase
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }
}
