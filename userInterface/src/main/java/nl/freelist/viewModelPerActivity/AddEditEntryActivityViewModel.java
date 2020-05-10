package nl.freelist.viewModelPerActivity;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.dto.ViewModelEvent;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.useCases.CommandHandler;
import nl.freelist.domain.valueObjects.Id;

public class AddEditEntryActivityViewModel extends AndroidViewModel {


  private Repository repository;

  public AddEditEntryActivityViewModel(
      @NonNull Application application) { //Todo: Application can be removed from constructor if context is passed into commands
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public Single<ViewModelEntry> getViewModelEntry(String uuid) {
    Single<ViewModelEntry> result = Single.fromCallable(
        () -> repository.getViewModelEntryById(Id.fromString(uuid)))
        .observeOn(Schedulers.io())
        .subscribeOn(Schedulers.io());
    return result;
  }

  public static Single<Result> handle(
      Command command) { //Todo: why use Single<Result> and not Completable?
    Single<Result> result = Single.fromCallable(
        () -> new CommandHandler()
            .execute(command))
//        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
        ;
    return result;
  }

  public Observable<List<ViewModelEvent>> getAllEventsFor(String uuid) { //For Entry history page
    Observable<List<ViewModelEvent>> eventList = Observable
        .fromCallable(
            () -> repository.getAllEventsForId(Id.fromString(uuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return eventList;
  }

}