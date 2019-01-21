package nl.freelist.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import java.util.UUID;
import nl.freelist.data.dto.DataEntry;

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
      entryDao.insert(new DataEntry(
          UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()),
          UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()),
          UUID.nameUUIDFromBytes(
              (UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()).toString() + "Shopping")
                  .getBytes()),
          "Shopping",
          "Description shopping",
          300));
//      entryDao.insert(new DataEntry("Work", "Description work", 300, 0));
//      entryDao.insert(new DataEntry("Private", "Description private", 300, 0));
//      entryDao.insert(new DataEntry("Sub 1 A", "Description 1A", 300, 1));
//      entryDao.insert(new DataEntry("Sub 1 B", "Description 1B", 300, 1));
//      entryDao.insert(new DataEntry("Sub 1 C", "Description 1C", 300, 1));
//      entryDao.insert(new DataEntry("Sub 1A A", "Description 1A A", 300, 4));
      return null;
    }
  }
}
