package nl.freelist.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.Entry;
import nl.freelist.crossCuttingConcerns.DateHelper;
import nl.freelist.crossCuttingConcerns.DurationHelper;
import nl.freelist.data.EntryDao;
import nl.freelist.data.EntryDatabase;

public class EntryRepository {

  private final EntryDao entryDao;
  private LiveData<List<Entry>> allEntries;

  public EntryRepository(Application application) {
    EntryDatabase database = EntryDatabase.getInstance(application);
    entryDao = database.entryDao();
  }

  private Entry makeEntryFromViewModelEntry(ViewModelEntry viewModelEntry) {
    int id = viewModelEntry.getId();
    String title = viewModelEntry.getTitle();
    String description = viewModelEntry.getDescription();
    int duration = DurationHelper.getDurationLongFromString(viewModelEntry.getDuration());
    long date = DateHelper.getLongFromString(viewModelEntry.getDate());
    boolean isCompletedStatus = viewModelEntry.getIsCompletedStatus();
    int parentId = viewModelEntry.getParentId();
    Entry entry = new Entry(id, title, description, duration, date, isCompletedStatus, parentId);
    return entry;
  }

  public void insert(ViewModelEntry viewModelEntry) {
    Entry entry = makeEntryFromViewModelEntry(viewModelEntry);
    new InsertEntryAsyncTask(entryDao).execute(entry);
  }

  public LiveData<ViewModelEntry> getViewModelEntry(int requestedEntryId) {
    LiveData<Entry> entry = entryDao.getEntry(requestedEntryId);
    LiveData<ViewModelEntry> viewModelEntry;
    viewModelEntry =
        Transformations.map(entry,
            newData -> createViewModelEntryFromEntry(newData));
    return viewModelEntry;
  }

  public LiveData<List<ViewModelEntry>> getAllEntries() {
    LiveData<List<ViewModelEntry>> allViewModelEntries;
    allEntries = entryDao.getAllEntries();
    allViewModelEntries = Transformations.map(allEntries,
        entryList -> createViewModelEntryListFromEntryList(entryList));
    return allViewModelEntries;
  }

  private List<ViewModelEntry> createViewModelEntryListFromEntryList(List<Entry> entryList) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    ViewModelEntry viewModelEntry;
    for (Entry entry : entryList) {
      viewModelEntry = createViewModelEntryFromEntry(entry);
      allViewModelEntries.add(viewModelEntry);
    }
    return allViewModelEntries;
  }

  private ViewModelEntry createViewModelEntryFromEntry(Entry entry) {
    int id = entry.getId();
    int parentId = entry.getId(); //Todo: implement getParentId()
    String parentTitle = entry.getTitle(); //Todo: implement getParentTitle
    String title = entry.getTitle();
    String description = entry.getDescription();
    String duration = entry.getFormattedDuration();
    String date = entry.getFormattedDate();
    Boolean isCompletedStatus = entry.getIsCompletedStatus();
    ViewModelEntry viewModelEntry = new ViewModelEntry(id, parentId, parentTitle, title,
        description, duration, date, isCompletedStatus);
    return viewModelEntry;
  }

  public void update(ViewModelEntry viewModelEntry) {
    Entry entry = makeEntryFromViewModelEntry(viewModelEntry);
    new UpdateEntryAsyncTask(entryDao).execute(entry);
  }

  public void delete(ViewModelEntry viewModelEntry) {
    Entry entry = makeEntryFromViewModelEntry(viewModelEntry);
    new DeleteEntryAsyncTask(entryDao).execute(entry);
  }

  public void deleteAllEntries() {
    new DeleteAllEntriesAsyncTask(entryDao).execute();
  }

  private static class InsertEntryAsyncTask extends AsyncTask<Entry, Void, Void> {

    private EntryDao entryDao;

    private InsertEntryAsyncTask(EntryDao entryDao) {
      this.entryDao = entryDao;
    }

    @Override
    protected Void doInBackground(Entry... entries) {
      entryDao.insert(entries[0]);
      return null;
    }
  }

  private static class UpdateEntryAsyncTask extends AsyncTask<Entry, Void, Void> {

    private EntryDao entryDao;

    private UpdateEntryAsyncTask(EntryDao entryDao) {
      this.entryDao = entryDao;
    }

    @Override
    protected Void doInBackground(Entry... entries) {
      entryDao.update(entries[0]);
      return null;
    }
  }

  private static class DeleteEntryAsyncTask extends AsyncTask<Entry, Void, Void> {

    private EntryDao entryDao;

    private DeleteEntryAsyncTask(EntryDao entryDao) {
      this.entryDao = entryDao;
    }

    @Override
    protected Void doInBackground(Entry... entries) {
      entryDao.delete(entries[0]);
      return null;
    }
  }

  static class DeleteAllEntriesAsyncTask extends AsyncTask<Void, Void, Void> {

    private EntryDao entryDao;

    private DeleteAllEntriesAsyncTask(EntryDao entryDao) {
      this.entryDao = entryDao;
    }

    @Override
    protected Void doInBackground(Void... voids) {
      entryDao.deleteAllEntries();
      return null;
    }
  }
}
