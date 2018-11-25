package nl.freelist.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;
import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

public class CalendarActivityViewModel extends AndroidViewModel {

  final private EntryRepository repository;
  final private LiveData<List<ViewModelEntry>> allEntries;

  public CalendarActivityViewModel(@NonNull Application application) {
    super(application);
    repository = new EntryRepository(application);
    allEntries = repository.getAllEntries();
  }

  public void insert(ViewModelEntry entry) {
    repository.insert(entry);
  }

  public void update(ViewModelEntry entry) {
    repository.update(entry);
  }

  public void delete(ViewModelEntry entry) {
    repository.delete(entry);
  }

  public void deleteAllEntries() {
    repository.deleteAllEntries();
  }

  public LiveData<List<ViewModelEntry>> getAllEntries() {
    return allEntries;
  }

}
