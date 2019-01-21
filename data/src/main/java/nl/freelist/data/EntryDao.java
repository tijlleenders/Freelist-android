package nl.freelist.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.dto.DataEntry;
import nl.freelist.data.dto.DataEntryExtra;
import nl.freelist.data.dto.UuidConverter;

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
      "SELECT uuid FROM DataEntry\n"
          + "WHERE DataEntry.parentUuid = :uuid;\n"
  )
  @TypeConverters(UuidConverter.class)
  List<UUID> getAllDirectChildrenIdsForParent(@TypeConverters(UuidConverter.class) UUID uuid);

  @Query(
      "WITH RECURSIVE ChildrenCTE(x) AS (\n"
          + "  SELECT  DataEntry.uuid\n"
          + "  FROM DataEntry\n"
          + "  WHERE DataEntry.uuid = :uuid\n"
          + "  UNION ALL\n"
          + "  SELECT  DataEntry.uuid\n"
          + "  FROM    DataEntry, ChildrenCTE cte\n"
          + "          WHERE DataEntry.parentUuid=cte.x LIMIT 10000\n"
          + ") \n"
          + "SELECT *\n"
          + ", (\n"
          + "\tSELECT count(*) FROM DataEntry\n"
          + "\tWHERE DataEntry.uuid != :uuid\n"
          + "\tAND DataEntry.uuid IN\n"
          + "\t(select * from ChildrenCTE)\n"
          + "\tAND DataEntry.uuid NOT IN\n"
          + "\t(select parentUuid from DataEntry)\n"
          + "\t) AS childrenCount\n"
          + ", (\n"
          + "\tSELECT sum(duration) FROM DataEntry\n"
          + "\tWHERE DataEntry.uuid != :uuid\n"
          + "\tAND DataEntry.uuid IN\n"
          + "\t(select * from ChildrenCTE)\n"
          + "\tAND DataEntry.uuid NOT IN\n"
          + "\t(select parentUuid from DataEntry)\n"
          + "\t) AS childrenDuration\n"
          + "FROM DataEntry\n"
          + "WHERE DataEntry.uuid = :uuid;")
  DataEntryExtra getDataEntryExtra(@TypeConverters(UuidConverter.class) UUID uuid);

}
