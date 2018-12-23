package nl.freelist.presentationConstants;

public class ActivityConstants {

  public static final String EXTRA_TITLE = "nl.freelist.EXTRA_TITLE";

  public static final String EXTRA_ENTRY_ID = "nl.freelist.EXTRA_ENTRY_ID";

  public static final String EXTRA_ENTRY_PARENT_ID = "nl.freelist.EXTRA_ENTRY_PARENT_ID";

  public static final String EXTRA_ENTRY_TITLE = "nl.freelist.EXTRA_ENTRY_TITLE";

  public static final String EXTRA_ENTRY_DESCRIPTION = "nl.freelist.EXTRA_ENTRY_DESCRIPTION";

  public static final String EXTRA_ENTRY_FORMATTED_DURATION =
      "nl.freelist.EXTRA_ENTRY_FORMATTED_DURATION";

  public static final String EXTRA_ENTRY_FORMATTED_DATE = "nl.freelist.EXTRA_ENTRY_FORMATTED_DATE";

  public static final int ADD_ENTRY_REQUEST =
      1; // todo: Can't I just use string instead of magic numbers? Alle intent Action codes are
  // constant strings right? StartActivityForResult requires an int requestCode....
  public static final String EXTRA_REQUEST_TYPE_ADD = "nl.freelist.REQUEST_TYPE_ADD";

  public static final int EDIT_ENTRY_REQUEST = 2;
  public static final String EXTRA_REQUEST_TYPE_EDIT = "nl.freelist.REQUEST_TYPE_EDIT";

  public static final int SUB_ENTRY_REQUEST = 3;
  public static final String EXTRA_REQUEST_TYPE_SUB_ENTRY = "nl.freelist.REQUEST_TYPE_SUB_ENTRY";

  // For rendering layout of different entry types
  public static final int SINGLE_ENTRY_VIEW = 0;
  public static final int MULTIPLE_ENTRY_VIEW = 1;
  public static final int PARENT_ENTRY_VIEW = 2;

  public static final int VIEWMODEL_ENTRY_ID_NOT_SET = 0;

}
