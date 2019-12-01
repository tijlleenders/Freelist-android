package nl.freelist.viewModelPerActivity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelCalendarOption;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.useCases.CommandHandler;

// Todo: rename (parent)Uuid to (parent)entryId

public class ChooseCalendarOptionViewModel extends AndroidViewModel {

  private String entryUuid;
  private String resourceUuid;
  private Repository repository;

  public ChooseCalendarOptionViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public static Single<Result> handle(
      Command command) { //Todo: why use Single<Result> and not Completable?
    Single<Result> result = Single.fromCallable(
        () -> new CommandHandler()
            .execute(command))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return result;
  }

  public Observable<List<ViewModelCalendarOption>> getAllPrioOptions(String entryUuid,
      String resourceUuid) {
    Observable<List<ViewModelCalendarOption>> prioEntryList = Observable
        .fromCallable(
            () -> repository.getAllPrioOptions(entryUuid, resourceUuid))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return prioEntryList;
  }

  public void updateEntryAndResource(String entryUuid, String resourceUuid) {
    this.entryUuid = entryUuid;
    this.resourceUuid = resourceUuid;
  }

}