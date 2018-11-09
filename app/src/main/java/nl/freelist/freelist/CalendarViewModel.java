package nl.freelist.freelist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

class CalendarViewModel extends AndroidViewModel {

    final private EntryRepository repository;
    final private LiveData<List<Entry>> allEntries;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        //repository = new EntryRepository(application);
        repository = EntryRepository.getInstance(application);
        allEntries = repository.getAllEntries();
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

    public void deleteAllEntries() {
        repository.deleteAllEntries();
    }

    public LiveData<List<Entry>> getAllEntries() {
        return allEntries;
    }

}
