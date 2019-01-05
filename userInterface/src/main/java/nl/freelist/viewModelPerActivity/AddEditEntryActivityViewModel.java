package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import nl.freelist.data.EntryRepository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.useCases.SaveEntry;

public class AddEditEntryActivityViewModel extends AndroidViewModel {

  private EntryRepository entryRepository;

  public AddEditEntryActivityViewModel(@NonNull Application application) {
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
  }

  public Single<ViewModelEntry> getViewModelEntry(int id) {
    Single<ViewModelEntry> result = Single.fromCallable(
        () -> entryRepository.getViewModelEntryById(id)).observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
    return result;
  }

  public Single<ResultObject<Entry>> saveViewModelEntry(ViewModelEntry viewModel) {
    Entry entryToSave = ViewModelEntry.getEntryFromViewModelEntry(viewModel);
    Single<ResultObject<Entry>> resultObject = Single.fromCallable(
        () -> new SaveEntry(entryRepository)
            .execute(entryToSave))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return resultObject;
  }

}