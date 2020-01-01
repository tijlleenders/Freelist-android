package nl.freelist.viewModelPerActivity;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.UUID;
import nl.freelist.commands.CreateResourceCommand;
import nl.freelist.data.Repository;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.useCases.CommandHandler;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

// Todo: rename (parent)Uuid to (parent)entryId

public class NavigateEntriesViewModel extends AndroidViewModel {

  private String parentUuid;
  private Repository repository;

  public NavigateEntriesViewModel(@NonNull Application application) {
    super(application);
    repository = new Repository(getApplication().getApplicationContext());
  }

  public void setParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public String getParentUuid() {
    return parentUuid;
  }

  @Override
  protected void onCleared() {
    //Todo: Unsubscribe if observing anything?
    super.onCleared();
  }

  public Observable<List<ViewModelEntry>> getAllChildrenEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> repository.getAllViewModelEntriesForParent(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public Observable<List<ViewModelEntry>> getBreadcrumbEntries() {
    Observable<List<ViewModelEntry>> viewModelEntryList = Observable
        .fromCallable(
            () -> repository.getBreadcrumbViewModelEntries(UUID.fromString(parentUuid)))
        .observeOn(Schedulers.io()).subscribeOn(Schedulers.io());
    return viewModelEntryList;
  }

  public void updateParentUuid(String parentUuid) {
    this.parentUuid = parentUuid;
  }

  public Observable<Boolean> deleteAllEntriesFromRepository() {
    Observable<Boolean> resultObservable = Observable
        .fromCallable(
            () -> repository.deleteAllEntriesFromRepository());
    return resultObservable;
  }

  public void delete(ViewModelEntry entryAt) {
    // Todo: implement with UseCase
  }

  public Result createResource(
      Email ownerEmail,
      Email resourceEmail,
      DateTimeRange lifetimeDateTimeRange
  ) {
    CreateResourceCommand createResourceCommand = new CreateResourceCommand(
        ownerEmail,
        resourceEmail,
        lifetimeDateTimeRange,
        repository
    );
    CommandHandler commandHandler = new CommandHandler();
    return commandHandler.execute(createResourceCommand);
  }
}
