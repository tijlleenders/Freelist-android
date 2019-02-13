package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.data.EntryRepository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.useCases.CommandHandler;

public class AddEditEntryActivityViewModel extends AndroidViewModel {


  private EntryRepository entryRepository;

  public AddEditEntryActivityViewModel(
      @NonNull Application application) { //Todo: Application can be removed from constructor if context is passed into commands
    super(application);
    entryRepository = new EntryRepository(getApplication().getApplicationContext());
  }

  public Single<ViewModelEntry> getViewModelEntry(String uuid) { //Todo: replace by Command
    Single<ViewModelEntry> result = Single.fromCallable(
        () -> entryRepository.getViewModelEntryById(UUID.fromString(uuid)))
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
    return result;
  }

  public Single<Result> handle(Command command) {
    Single<Result> result = Single.fromCallable(
        () -> new CommandHandler()
            .execute(command))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return result;
  }


}