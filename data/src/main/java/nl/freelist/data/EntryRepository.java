package nl.freelist.data;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
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
    int id = entry.getId();
    String title = entry.getTitle();
    String description = entry.getDescription();
    int duration = entry.getDuration();
    int parentId = entry.getParentId();
    DataEntry dataEntry = new DataEntry(id, title, description, duration, parentId);
    return dataEntry;
  }

  private List<Entry> getEntryListFromDataEntryList(List<DataEntry> dataEntryList) {
    List<Entry> tempEntryList = new ArrayList<>();
    Entry entry;
    for (DataEntry dataEntry : dataEntryList) {
      entry = getEntryFromDataEntry(dataEntry);
      tempEntryList.add(entry);
    }
    return tempEntryList;
  }

  private Entry getEntryFromDataEntry(DataEntry dataEntry) {
    int id = dataEntry.getId();
    int parentId = dataEntry.getParentId();
    String title = dataEntry.getTitle();
    String description = dataEntry.getDescription();
    int duration = dataEntry.getDuration();
    Entry entry = new Entry(id, parentId, title, description, duration);
    return entry;
  }

  private Entry getEntryFromDataEntryExtra(DataEntryExtra dataEntryExtra) {
    int id = dataEntryExtra.getId();
    int parentId = dataEntryExtra.getParentId();
    String title = dataEntryExtra.getTitle();
    String description = dataEntryExtra.getDescription();
    int duration = dataEntryExtra.getDuration();
    int childrenCount = dataEntryExtra.getChildrenCount();
    int childrenDuration = dataEntryExtra.getChildrenDuration();
    Entry entry =
        new Entry(
            id,
            parentId,
            title,
            description,
            duration,
            childrenCount,
            childrenDuration);
    return entry;
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
  public Entry getById(int id) {
    return getEntryFromDataEntry(entryDao.getEntry(id));
  }

  @Override
  public List<Entry> getAllEntries() {
    return getEntryListFromDataEntryList(entryDao.getAllEntries());
  }

  @Override
  public List<Entry> getAllEntriesForParent(int id) {
    List<Integer> idList = entryDao.getAllAncestorAndDirectChildrenIdsForParent(id);
    List<Entry> entryList = new ArrayList<>();
    for (int idToFetch : idList) { // Todo: Fix n+1 query if possible/needed performance wise
      entryList.add(getEntryFromDataEntryExtra(entryDao.getDataEntryExtra(idToFetch)));
    }
    return entryList;
  }
}
