package nl.freelist.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import nl.freelist.database.Entry;
import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

public class TestViewModelActivityViewModel extends AndroidViewModel {

  final private EntryRepository repository;
  final private LiveData<ViewModelEntry> viewModelEntry;

  public TestViewModelActivityViewModel(@NonNull Application application) {
    super(application);
    repository = EntryRepository.getInstance(application);
    viewModelEntry = repository.getViewModelEntry(1);
  }

  public void insert(Entry entry) {
    repository.insert(entry);
  }

  public void update(Entry entry) {
    repository.update(entry);
  }

  public void delete(Entry entry) {
    repository.delete(entry);
  }

  public LiveData<ViewModelEntry> getEntry() {
    return viewModelEntry;
  }
}

