package nl.freelist.data;

import android.content.ContentValues;

public class InsertQuery {

  private String table;
  private ContentValues contentValues;

  InsertQuery(String table, ContentValues contentValues) {
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
