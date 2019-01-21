package nl.freelist.data.dto;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;
import java.util.UUID;

@Entity(tableName = "DataEntry")
public class DataEntry {

  @ColumnInfo(name = "ownerUuid", typeAffinity = ColumnInfo.BLOB)
  @TypeConverters(UuidConverter.class)
  private UUID ownerUuid;

  public void setOwnerUuid(UUID ownerUuid) {
    this.ownerUuid = ownerUuid;
  }

  public void setParentUuid(UUID parentUuid) {
    this.parentUuid = parentUuid;
  }

  @ColumnInfo(name = "parentUuid", typeAffinity = ColumnInfo.BLOB)
  @TypeConverters(UuidConverter.class)
  private UUID parentUuid;

  @NonNull
  @PrimaryKey
  @ColumnInfo(name = "uuid", typeAffinity = ColumnInfo.BLOB)
  @TypeConverters(UuidConverter.class)
  private UUID uuid;
  private String title;
  private String description;
  private int duration;

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  @Ignore
  public DataEntry() throws Exception {
    throw new Exception("Default constructor for DataEntry not allowed.");
  }

  public DataEntry(
      UUID ownerUuid, UUID parentUuid, UUID uuid, String title, String description, int duration) {
    this.ownerUuid = ownerUuid;
    this.parentUuid = parentUuid;
    this.uuid = uuid;
    this.title = title;
    this.description = description;
    this.duration = duration;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public void setParentUuid(int parent) {
    this.parentUuid = parentUuid;
  }

  public UUID getParentUuid() {
    return parentUuid;
  }

  public UUID getOwnerUuid() {
    return ownerUuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public int getDuration() {
    return duration;
  }
}
