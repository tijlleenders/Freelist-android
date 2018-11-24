package nl.freelist.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.os.AsyncTask;

import android.view.View;
import java.util.List;
import nl.freelist.database.Entry;
import nl.freelist.database.EntryDao;
import nl.freelist.database.EntryDatabase;

public class EntryRepository {

  private final EntryDao entryDao;
  private LiveData<List<Entry>> allEntries;

  private static EntryRepository instance;

  private EntryRepository(Application application) {
    EntryDatabase database = EntryDatabase.getInstance(application);
    entryDao = database.entryDao();
    allEntries = entryDao.getAllEntries();
  }

  public static synchronized EntryRepository getInstance(Application application) {
    if (instance == null) {
      instance = new EntryRepository(application);
    }
    return instance;
  }

  public void insert(Entry entry) {
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

  public void update(Entry entry) {
    new UpdateEntryAsyncTask(entryDao).execute(entry);
  }

  public void delete(Entry entry) {
    new DeleteEntryAsyncTask(entryDao).execute(entry);
  }

  public void deleteAllEntries() {
    new DeleteAllEntriesAsyncTask(entryDao).execute();
  }

  public LiveData<List<Entry>> getAllEntries() {
    return allEntries;
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
