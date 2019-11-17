package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.useCases.CommandHandler;

public class AddEditEntryActivityViewModel extends AndroidViewModel {


  private Repository repository;

  public AddEditEntryActivityViewModel(
      @NonNull Application application) { //Todo: Application can be removed from constructor if context is passed into commands
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public Single<ViewModelEntry> getViewModelEntry(String uuid) { //Todo: replace by Command
    Single<ViewModelEntry> result = Single.fromCallable(
        () -> repository.getViewModelEntryById(UUID.fromString(uuid)))
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


  public Completable scheduleEntry(String uuid, //Todo: replace by Command ??
      String resource) { //Todo: why don't I use .fromCallable? Because repository didn't use rx before?
    Completable resultCompletable;
    resultCompletable = repository.scheduleEntry(uuid, resource);
    return resultCompletable;
  }
}