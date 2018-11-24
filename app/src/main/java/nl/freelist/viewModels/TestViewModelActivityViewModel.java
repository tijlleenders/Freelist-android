package nl.freelist.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import android.view.View;
import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

public class TestViewModelActivityViewModel extends AndroidViewModel {

  final private EntryRepository repository;
  private LiveData<ViewModelEntry> viewModelEntry;

  public TestViewModelActivityViewModel(@NonNull Application application) {
    super(application);
    repository = EntryRepository.getInstance(application);
    viewModelEntry = repository.getViewModelEntry(1);
  }

  public LiveData<ViewModelEntry> getViewModelEntry(int id) {
    viewModelEntry = repository.getViewModelEntry(id);
    return viewModelEntry;
  }

}