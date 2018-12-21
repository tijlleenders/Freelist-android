package nl.freelist.viewModels;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel
import android.app.Application;
import android.support.annotation.NonNull;
import android.arch.lifecycle.AndroidViewModel;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.GetEntryByIdUseCase;
import nl.freelist.data.EntryRepository;

public class AddEditEntryActivityViewModel extends AndroidViewModel {

  public AddEditEntryActivityViewModel(@NonNull Application application) {
    super(application);
  }

  public Single<ViewModelEntry> getViewModelEntry(int id) {
    Single<Entry> tempEntry = Single.fromCallable(
        () -> new GetEntryByIdUseCase(new EntryRepository(getApplication().getApplicationContext()))
            .execute(id)).observeOn(AndroidSchedulers
        .mainThread()).subscribeOn(Schedulers.io());
    Single<ViewModelEntry> result = tempEntry
        .map(entry -> ViewModelEntry.getViewModelEntryFromEntry(entry));
    return result;
  }


  //Todo: set save method

}