package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.data.EntryRepository;
import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.GetEntryByIdUseCase;
import nl.freelist.domain.useCases.SaveEntry;
import nl.freelist.viewModelPerEntity.ViewModelEntry;

public class AddEditEntryActivityViewModel extends AndroidViewModel {

  private EntryRepository entryRepository;

  public AddEditEntryActivityViewModel(@NonNull Application application) {
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
  }

  public Single<ViewModelEntry> getViewModelEntry(int id) {
    Single<Entry> tempEntry = Single.fromCallable(
        () -> new GetEntryByIdUseCase(entryRepository)
            .execute(id)).observeOn(AndroidSchedulers
        .mainThread()).subscribeOn(Schedulers.io());
    Single<ViewModelEntry> result = tempEntry
        .map(entry -> ViewModelEntry.getViewModelEntryFromEntry(entry));
    return result;
  }

  public Single<ResultObject<Entry>> saveViewModelEntry(ViewModelEntry viewModel) {
    Entry entryToSave = ViewModelEntry.getEntryFromViewModelEntry(viewModel);
    Single<ResultObject<Entry>> resultObject = Single.fromCallable(
        () -> new SaveEntry(entryRepository)
            .execute(entryToSave))
        .observeOn(AndroidSchedulers
            .mainThread()).subscribeOn(Schedulers.io());
    return resultObject;
  }

}