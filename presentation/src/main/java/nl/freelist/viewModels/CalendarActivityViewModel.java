package nl.freelist.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.GetAllEntriesUseCase;
import nl.freelist.data.EntryRepository;


public class CalendarActivityViewModel extends AndroidViewModel {


  public CalendarActivityViewModel(@NonNull Application application) {
    super(application);
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Single<List<ViewModelEntry>> getAllEntries() {
    Single<List<Entry>> temp = Single.fromCallable(() -> new GetAllEntriesUseCase(
        new EntryRepository(getApplication().getApplicationContext())).execute())
        .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    Single<List<ViewModelEntry>> result = temp
        .map(entries -> ViewModelEntry.createViewModelEntryListFromEntryList(entries));
    return result;
  }

  public void deleteAllEntries() {
    // Todo: implement with UseCase
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }
}
