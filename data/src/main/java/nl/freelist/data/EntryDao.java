package nl.freelist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface EntryDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(DataEntry dataEntry);

  @Update
  void update(DataEntry dataEntry);

  @Delete
  void delete(DataEntry dataEntry);

  @Query("DELETE FROM DataEntry")
  void deleteAllEntries();

  @Query("SELECT * FROM DataEntry ORDER BY duration ASC")
  List<DataEntry> getAllEntries();

  @Query("SELECT * FROM DataEntry WHERE parent = :id OR id = :id ORDER BY duration ASC")
  List<DataEntry> getAllEntriesForParent(int id);

  @Query("SELECT * FROM DataEntry WHERE id = :id")
  DataEntry getEntry(int id);

}
