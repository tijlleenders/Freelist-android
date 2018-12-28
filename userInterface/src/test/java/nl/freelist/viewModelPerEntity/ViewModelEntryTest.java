package nl.freelist.viewModelPerEntity;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.entities.Entry;
import nl.freelist.presentationConstants.ActivityConstants;
import org.junit.Before;
import org.junit.Test;

public class ViewModelEntryTest {

  List<Entry> entryList = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    entryList.add(new Entry(1, 0, "todo", "Title 1", "Description 1", 300));
    entryList.add(new Entry(2, 0, "todo", "Title 2", "Description 2", 300));
    entryList.add(new Entry(3, 0, "todo", "Title 3", "Description 3", 300));
    entryList.add(new Entry(4, 1, "todo", "Sub 1 A", "Description 1A", 300));
    entryList.add(new Entry(5, 1, "todo", "Sub 1 B", "Description 1B", 300));
    entryList.add(new Entry(6, 1, "todo", "Sub 1 C", "Description 1C", 300));
    entryList.add(new Entry(7, 4, "todo", "Sub 1A A", "Description 1A A", 300));
  }

  @Test
  public void createViewModelEntryListFromEntryList() {
    List<ViewModelEntry> viewModelEntryList = new ArrayList<>();
    viewModelEntryList = ViewModelEntry.createViewModelEntryListFromEntryList(entryList, 1);

    assertEquals(ActivityConstants.NODE_ENTRY_VIEW_TYPE, viewModelEntryList.get(0).getType());
    assertEquals(ActivityConstants.LEAF_ENTRY_VIEW_TYPE, viewModelEntryList.get(1).getType());
    assertEquals(ActivityConstants.LEAF_ENTRY_VIEW_TYPE, viewModelEntryList.get(2).getType());
    assertEquals(ActivityConstants.NODE_ENTRY_VIEW_TYPE, viewModelEntryList.get(3).getType());
    assertEquals(ActivityConstants.LEAF_ENTRY_VIEW_TYPE, viewModelEntryList.get(4).getType());
    assertEquals(ActivityConstants.LEAF_ENTRY_VIEW_TYPE, viewModelEntryList.get(5).getType());
    assertEquals(ActivityConstants.LEAF_ENTRY_VIEW_TYPE, viewModelEntryList.get(6).getType());

  }
}