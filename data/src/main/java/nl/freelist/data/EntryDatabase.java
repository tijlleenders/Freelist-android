package nl.freelist.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {DataEntry.class}, version = 1)
public abstract class EntryDatabase extends RoomDatabase {

  public abstract EntryDao entryDao();

  private static EntryDatabase instance;

  public static synchronized EntryDatabase getInstance(Context appContext) {
    if (instance == null) {
      instance = Room.databaseBuilder(appContext
          , EntryDatabase.class, "entry_database")
          .fallbackToDestructiveMigration()
          .addCallback(roomCallback)
          .build();
    }
    return instance;
  }

  final private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
      super.onCreate(db);
      new PopulateDbAsyncTask(instance).execute();
    }
  };

  private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

    final private EntryDao entryDao;

    private PopulateDbAsyncTask(EntryDatabase db) {
      entryDao = db.entryDao();
    }

    @Override
    protected Void doInBackground(Void... voids) {
      entryDao.insert(new DataEntry("Title 1", "Description 1", 1, 1539456867, false, 0));
      entryDao.insert(new DataEntry("Title 2", "Description 2", 2, 1539456867, false, 0));
      entryDao.insert(new DataEntry("Title 3", "Description 3", 3, 1539456867, false, 0));
      return null;
    }
  }
}
