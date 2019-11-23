package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
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

  public Single<Result> handle(
      Command command) { //Todo: why use Single<Result> and not Completable?
    Single<Result> result = Single.fromCallable(
        () -> new CommandHandler()
            .execute(command))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return result;
  }

  public Observable<List<ViewModelEvent>> getAllEventsFor(String uuid) {
    Observable<List<ViewModelEvent>> calendarEntryList = Observable
        .fromCallable(
            () -> repository.getAllEventsForId(uuid))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return calendarEntryList;
  }

}