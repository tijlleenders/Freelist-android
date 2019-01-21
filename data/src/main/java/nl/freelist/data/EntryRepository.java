package nl.freelist.data;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.dto.DataEntry;
import nl.freelist.data.dto.DataEntryExtra;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Specifiable;

public class EntryRepository implements nl.freelist.domain.interfaces.Repository<Entry> {

  private final EntryDao entryDao;

  public EntryRepository(Context appContext) {
    EntryDatabase database = EntryDatabase.getInstance(appContext);
    entryDao = database.entryDao();
  }

  private DataEntry getDataEntryFromEntry(Entry entry) {
    UUID ownerUuid = entry.getOwnerUuid();
    UUID parentUuid = entry.getParentUuid();
    UUID uuid = entry.getUuid();
    String title = entry.getTitle();
    String description = entry.getDescription();
    int duration = entry.getDuration();
    DataEntry dataEntry = new DataEntry(ownerUuid, parentUuid, uuid, title, description, duration);
    return dataEntry;
  }


  private Entry getEntryFromDataEntryExtra(DataEntryExtra dataEntryExtra) {
    UUID ownerUuid = dataEntryExtra.getOwnerUuid();
    UUID uuid = dataEntryExtra.getUuid();
    UUID parentUuid = dataEntryExtra.getParentUuid();
    String title = dataEntryExtra.getTitle();
    String description = dataEntryExtra.getDescription();
    int duration = dataEntryExtra.getDuration();
    int childrenCount = dataEntryExtra.getChildrenCount();
    int childrenDuration = dataEntryExtra.getChildrenDuration();
    Entry entry =
        new Entry(
            ownerUuid,
            parentUuid,
            uuid,
            title,
            description,
            duration,
            childrenCount,
            childrenDuration);
    return entry;
  }

  private ViewModelEntry getViewModelEntryFromDataEntryExtra(DataEntryExtra dataEntryExtra,
      int type) { ////Todo: refactor to method in DataEntryExtra so it doesn't have to expose internals
    String ownerUuid = dataEntryExtra.getOwnerUuid().toString();
    String parentUuid = dataEntryExtra.getParentUuid().toString();
    String uuid = dataEntryExtra.getUuid().toString();
    String title = dataEntryExtra.getTitle();
    String description = dataEntryExtra.getDescription();
    int duration = dataEntryExtra.getDuration();
    int childrenCount = dataEntryExtra.getChildrenCount();
    int childrenDuration = dataEntryExtra.getChildrenDuration();
    if (childrenCount > 0) {
      type = Constants.STACK_ENTRY_VIEW_TYPE;
    } else {
      type = Constants.SINGLE_ENTRY_VIEW_TYPE;
    }

    ViewModelEntry viewModelEntry =
        new ViewModelEntry(
            ownerUuid,
            parentUuid,
            uuid,
            title,
            description,
            duration,
            type,
            childrenCount,
            childrenDuration);
    return viewModelEntry;
  }

  @Override
  public ResultObject<Entry> insert(Entry entry) {
    DataEntry dataEntry = getDataEntryFromEntry(entry);
    entryDao.insert(dataEntry);
    return new ResultObject<>(true);
  }

  @Override
  public void insert(Iterable<Entry> items) {
  }

  @Override
  public void update(Entry item) {
  }

  @Override
  public void delete(Entry item) {
  }

  @Override
  public void delete(Specifiable specification) {
  }

  @Override
  public List<Entry> query(Specifiable specification) {
    List<Entry> allEntries = new ArrayList<>();
    return allEntries;
  }

  @Override
  public Entry getById(UUID uuid) {
    return getEntryFromDataEntryExtra(entryDao.getDataEntryExtra(uuid));
  }

  public ViewModelEntry getViewModelEntryById(UUID uuid) {
    return getViewModelEntryFromDataEntryExtra(entryDao.getDataEntryExtra(uuid),
        Constants.UNKNOWN_ENTRY_VIEW_TYPE);
  }

  public List<ViewModelEntry> getBreadcrumbViewModelEntries(UUID parentUuid) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    if (!parentUuid.equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()))) {
      ViewModelEntry parentViewModelEntry =
          getViewModelEntryFromDataEntryExtra(
              entryDao.getDataEntryExtra(parentUuid), Constants.PARENT_ENTRY_VIEW_TYPE);
      allViewModelEntries.add(parentViewModelEntry);

      if (!UUID.fromString(parentViewModelEntry.getParentUuid())
          .equals(UUID.nameUUIDFromBytes("tijl.leenders@gmail.com".getBytes()))) {
        ViewModelEntry parentOfParentViewModelEntry =
            getViewModelEntryFromDataEntryExtra(
                entryDao.getDataEntryExtra(UUID.fromString(parentViewModelEntry.getParentUuid())),
                Constants.PARENT_ENTRY_VIEW_TYPE);
        allViewModelEntries.add(0, parentOfParentViewModelEntry);
      }
    }
    return allViewModelEntries;
  }

  public List<ViewModelEntry> getAllViewModelEntriesForParent(UUID parentUuid) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();

    List<UUID> childrenUuids = entryDao
        .getAllDirectChildrenIdsForParent(parentUuid);//get children id list
    for (UUID childUuid : childrenUuids) {
      allViewModelEntries
          .add(getViewModelEntryFromDataEntryExtra(entryDao.getDataEntryExtra(childUuid),
              Constants.CHILD_ENTRY_VIEW_TYPE));
    }
    return allViewModelEntries;
  }

}
