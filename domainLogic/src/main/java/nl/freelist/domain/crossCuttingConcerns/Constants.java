package nl.freelist.domain.crossCuttingConcerns;

public class Constants {

  public static final String SETTINGS_USER_UUID = "nl.freelist.SETTINGS_USER_UUID";

  public static final String SETTINGS_PERSON_UUID = "nl.freelist.SETTINGS_RESOURCE_UUID";

  public static final String EXTRA_TITLE = "nl.freelist.EXTRA_TITLE";

  public static final String EXTRA_ENTRY_ID = "nl.freelist.EXTRA_ENTRY_ID";

  public static final String EXTRA_RESOURCE_ID = "nl.freelist.EXTRA_RESOURCE_ID";

  public static final String EXTRA_ENTRY_PARENT_ID = "nl.freelist.EXTRA_ENTRY_PARENT_ID";

  public static final int ADD_ENTRY_REQUEST =
      1; // todo: Can't I just use string instead of magic numbers? Alle intent Action codes are
  // constant strings right? StartActivityForResult requires an int requestCode....
  public static final String EXTRA_REQUEST_TYPE_ADD = "nl.freelist.REQUEST_TYPE_ADD";

  public static final int EDIT_ENTRY_REQUEST = 2;
  public static final String EXTRA_REQUEST_TYPE_EDIT = "nl.freelist.REQUEST_TYPE_EDIT";

  public static final int CHOOSE_PARENT_REQUEST = 3;
  public static final String EXTRA_REQUEST_TYPE_CHOOSE_PARENT = "nl.freelist.REQUEST_TYPE_CHOOSE_PARENT";

  public static final String EXTRA_PRIO_CHOSEN = "nl.freelist.PRIO_CHOSEN";

  // For rendering layout of different entry types
  public static final int SINGLE_ENTRY_VIEW_TYPE = 4;
  public static final int STACK_ENTRY_VIEW_TYPE = 5;

  public static final int CALENDAR_ENTRY_DATE_VIEW_TYPE = 0;
  public static final int CALENDAR_ENTRY_TODO_VIEW_TYPE = 1;

  public static final int EVENT_TYPE = 6;

  public static final int CHOOSE_CALENDAR_OPTION_REQUEST = 7;

  public static final int PRIO_NO_PROBLEM_TYPE = 8;
  public static final int PRIO_ONLY_RESCHEDULES_TYPE = 9;
  public static final int PRIO_PROBLEM_TYPE = 10;
}
