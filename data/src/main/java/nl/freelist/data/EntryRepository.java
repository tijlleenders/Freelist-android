package nl.freelist.data;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
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
    int id = entry.getId();
    String title = entry.getTitle();
    String description = entry.getDescription();
    int duration = entry.getDuration();
    int parentId = entry.getParentId();
    DataEntry dataEntry = new DataEntry(id, title, description, duration, parentId);
    return dataEntry;
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

  private ViewModelEntry getViewModelEntryFromDataEntryExtra(DataEntryExtra dataEntryExtra,
      int type) {
    int id = dataEntryExtra.getId();
    int parentId = dataEntryExtra.getParentId();
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
            id,
            parentId,
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
  public Entry getById(int id) {
    return getEntryFromDataEntryExtra(entryDao.getDataEntryExtra(id));
  }

  public ViewModelEntry getViewModelEntryById(int id) {
    return getViewModelEntryFromDataEntryExtra(entryDao.getDataEntryExtra(id),
        Constants.UNKNOWN_ENTRY_VIEW_TYPE);
  }

  public List<ViewModelEntry> getBreadcrumbViewModelEntries(int parentId) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();
    if (parentId != 0) {
      ViewModelEntry parentViewModelEntry =
          getViewModelEntryFromDataEntryExtra(
              entryDao.getDataEntryExtra(parentId), Constants.PARENT_ENTRY_VIEW_TYPE);
      allViewModelEntries.add(parentViewModelEntry);

      if (parentViewModelEntry.getParentId() != 0) {
        ViewModelEntry parentOfParentViewModelEntry =
            getViewModelEntryFromDataEntryExtra(
                entryDao.getDataEntryExtra(parentViewModelEntry.getParentId()),
                Constants.PARENT_ENTRY_VIEW_TYPE);
        allViewModelEntries.add(0, parentOfParentViewModelEntry);
      }
    }
    return allViewModelEntries;
  }

  public List<ViewModelEntry> getAllViewModelEntriesForParent(int parentId) {
    List<ViewModelEntry> allViewModelEntries = new ArrayList<>();

    List<Integer> childrenIds = entryDao
        .getAllDirectChildrenIdsForParent(parentId);//get children id list
    for (int childId : childrenIds) {
      allViewModelEntries
          .add(getViewModelEntryFromDataEntryExtra(entryDao.getDataEntryExtra(childId),
              Constants.CHILD_ENTRY_VIEW_TYPE));
    }
    return allViewModelEntries;
  }

}
