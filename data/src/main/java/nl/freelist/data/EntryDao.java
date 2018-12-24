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

  @Query(
      "SELECT * FROM DataEntry\n"
          + "WHERE DataEntry.id IN \n"
          + "(\n"
          + "WITH RECURSIVE parents(x) AS (\n"
          + "            SELECT :id\n"
          + "                UNION ALL\n"
          + "            SELECT DataEntry.parentId \n"
          + "            FROM DataEntry, parents \n"
          + "            WHERE DataEntry.id=parents.x AND DataEntry.parentId IS NOT NULL AND DataEntry.parentId != 0 LIMIT 10000\n"
          + "        )\n"
          + "        SELECT * FROM parents\n"
          + ")\n"
          + "UNION\n"
          + "SELECT * FROM DataEntry\n"
          + "WHERE DataEntry.parentId = :id;\n"
  )
  List<DataEntry> getAllEntriesForParent(int id);

  @Query("SELECT * FROM DataEntry WHERE id = :id")
  DataEntry getEntry(int id);

}
