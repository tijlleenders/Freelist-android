package nl.freelist.viewModels;

// OK according to https://developer.android.com/topic/libraries/architecture/viewmodel
import android.app.Application;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import android.view.View;
import nl.freelist.domain.GetEntryByIdUseCase;
import nl.freelist.domain.UseCase.Callback;
import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

// When we're in a ViewModel, we don't want to observeLiveData instances (and we can't because we
// have no LifecycleOwner around). ???
// However, we might want to react to them, or even combine (and mediate) multiple LiveData objects.
// For those situations, MediatorLiveData is what we're looking for.

// LiveData:
// The advantage, in short, is that you don’t need to manually cancel subscriptions between View and
// ViewModel.
// The observable paradigm works really well between the View controller and the ViewModel, so you
// can use it to observe other components of your app and take advantage of lifecycle awareness. For
// example:
//
// Observe changes in SharedPreferences
// Observe a document or collection in Firestore
// Observe the current user with an Authentication SDK like FirebaseAuth
// Observe a query in Room (which supports LiveData out of the box)

public class AddEditEntryActivityViewModel extends AndroidViewModel implements Callback {

  private GetEntryByIdUseCase getEntryByIdUseCase;
  private MediatorLiveData<ViewModelEntry> viewModelEntryMediatorLiveData;

  public AddEditEntryActivityViewModel(@NonNull Application application) {
    super(application);
    getEntryByIdUseCase = new GetEntryByIdUseCase(
        new EntryRepository(application.getApplicationContext())); //Todo:Fix dependency injection?
  }

  public MediatorLiveData<ViewModelEntry> getViewModelEntryMediatorLiveData() {
    return viewModelEntryMediatorLiveData;
  }

  public void setViewModelEntryId(int id) {
    getEntryByIdUseCase.execute(id, this);
  }

  @Override
  public void onSuccess(Object returnValue) {
    viewModelEntryMediatorLiveData = (MediatorLiveData<ViewModelEntry>) returnValue; //Todo: casting not nice, find solution
  }

  @Override
  public void onError(Throwable throwable) {

  }

  //Todo: set save method

}