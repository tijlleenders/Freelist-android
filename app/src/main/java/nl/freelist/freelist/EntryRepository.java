package nl.freelist.freelist;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

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
