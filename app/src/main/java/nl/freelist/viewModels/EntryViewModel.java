package nl.freelist.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import nl.freelist.database.Entry;
import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

public class EntryViewModel extends AndroidViewModel {

  final private EntryRepository repository;
  final private LiveData<ViewModelEntry> entry;

  public EntryViewModel(@NonNull Application application) {
    super(application);
    repository = EntryRepository.getInstance(application);
    entry = repository.getViewModelEntry(1);
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

}

