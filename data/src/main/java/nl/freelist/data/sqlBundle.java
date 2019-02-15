package nl.freelist.data;

import android.content.ContentValues;

public class sqlBundle {

  private String table;
  private ContentValues contentValues;

  sqlBundle(String table, ContentValues contentValues) {
    this.table = table;
    this.contentValues = contentValues;
  }

  public String getTable() {
    return table;
  }

  public ContentValues getContentValues() {
    return contentValues;
  }
}
