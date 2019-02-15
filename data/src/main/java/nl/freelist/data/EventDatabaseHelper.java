package nl.freelist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

public class EventDatabaseHelper extends SQLiteOpenHelper {

  private static final String TAG = "EventDatabaseHelper";

  private static EventDatabaseHelper instance = null;
  public static final int DATABASE_VERSION = 1;
  public static final String DATABASE_NAME = "EventDatabase.db";
  private Context context;
  private static SQLiteDatabase db;

  public static synchronized EventDatabaseHelper getInstance(Context context) {
    Log.d(TAG, "getInstance called.");
    if (instance == null) {
      instance = new EventDatabaseHelper(context);
      db = instance.getWritableDatabase();
    }
    return instance;
  }

  private EventDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
    Log.d(TAG, "constructor called.");
  }

  public void onCreate(SQLiteDatabase db) {
    Log.d(TAG, "onCreate called.");
    db.beginTransaction();
    try {
      db.execSQL(
          "CREATE TABLE IF NOT EXISTS events(\n"
              + "   occurredDateTime TEXT\n"
              + "   , persistedDateTime TEXT DEFAULT CURRENT_TIMESTAMP\n"
              + "   , aggregateId TEXT\n"
              + "   , type TEXT\n"
              + "   , data TEXT\n"
              + "   , eventSequenceNumber INTEGER\n)");
      db.execSQL(
          "CREATE TABLE IF NOT EXISTS aggregates(\n"
              + "   aggregateId TEXT\n"
              + "   , type TEXT\n"
              + "   , lastSavedEventSequenceNumber INTEGER\n"
              + "   , PRIMARY KEY(`aggregateId`))");
      db.execSQL(
          "CREATE TABLE IF NOT EXISTS viewModelEntry (\n"
              + "   `uuid` TEXT NOT NULL\n"
              + "   , `parentUuid` TEXT NOT NULL\n"
              + "   , `ownerUuid` TEXT NOT NULL\n"
              + "   , `title` TEXT\n"
              + "   , `description` TEXT\n"
              + "   , `duration` INTEGER NOT NULL DEFAULT 0\n"
              + "   , `childrenCount` INTEGER NOT NULL DEFAULT 0"
              + "   , `childrenDuration` INTEGER NOT NULL DEFAULT 0"
              + "   , `lastSavedEventSequenceNumber` INTEGER"
              + "   , PRIMARY KEY(`uuid`))");
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing onCreate");
    } finally {
      db.endTransaction();
    }
  }

  public List<sqlBundle> getQueriesForEvent(
      String aggregateIdType, int expectedLastSavedEventSequenceNumber, Event event)
      throws Exception {
    Log.d(TAG, "getQueriesForEvent called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    int lastSavedEventSequenceNumber = selectLastSavedEventSequenceNumber(event.getEntryId());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    if (lastSavedEventSequenceNumber != expectedLastSavedEventSequenceNumber) {
      throw new Exception("Optimistic concurrency exception");
    }

    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", event.getEntryId());
    aggregateContentValues.put("type", aggregateIdType);
    aggregateContentValues.put("lastSavedEventSequenceNumber", lastSavedEventSequenceNumber + 1);

    sqlBundleList.add(new sqlBundle("aggregates", aggregateContentValues));

    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", event.getEntryId());
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", lastSavedEventSequenceNumber + 1);

    sqlBundleList.add(new sqlBundle("events", eventContentValues));

    if (event.getClass().getSimpleName().equals("EntryCreatedEvent")) {
      EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;
      if (!entryCreatedEvent.getParentUuid().equals(entryCreatedEvent.getOwnerUuid())) {
        ViewModelEntry newParentEntry =
            viewModelEntryFor(entryCreatedEvent.getParentUuid());
        ContentValues newParentEntryContentValues = new ContentValues();
        newParentEntryContentValues.put("uuid", newParentEntry.getUuid());
        newParentEntryContentValues
            .put("lastSavedEventSequenceNumber", newParentEntry.getLastSavedEventSequenceNumber());
        newParentEntryContentValues.put("childrenCount", newParentEntry.getChildrenCount() + 1);
        sqlBundleList.add(new sqlBundle("viewModelEntry", newParentEntryContentValues));
      }
    }

    if (event.getClass().getSimpleName().equals("EntryParentChangedEvent")) {
      EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
      boolean fromRoot = false;
      boolean toRoot = false;
      ViewModelEntry oldParentEntry = null;
      ViewModelEntry newParentEntry = null;
      ViewModelEntry childEntry = viewModelEntryFor(entryParentChangedEvent.getEntryId());
      if (entryParentChangedEvent.getParentBefore().equals(childEntry.getOwnerUuid())) {
        fromRoot = true;
      } else {
        oldParentEntry =
            viewModelEntryFor(entryParentChangedEvent.getParentBefore());
      }
      if (entryParentChangedEvent.getParentAfter().equals(childEntry.getOwnerUuid())) {
        toRoot = true;
      } else {
        newParentEntry =
            viewModelEntryFor(entryParentChangedEvent.getParentAfter());
      }

      if (!fromRoot) {
        ContentValues oldParentEntryContentValues = new ContentValues();
        oldParentEntryContentValues.put("uuid", oldParentEntry.getUuid());
        oldParentEntryContentValues
            .put("lastSavedEventSequenceNumber", oldParentEntry.getLastSavedEventSequenceNumber());
        oldParentEntryContentValues.put("childrenCount", oldParentEntry.getChildrenCount() - 1);
        oldParentEntryContentValues.put(
            "childrenDuration", oldParentEntry.getChildrenDuration() - childEntry.getDuration());
        sqlBundleList.add(new sqlBundle("viewModelEntry", oldParentEntryContentValues));
      }

      if (!toRoot) {
        ContentValues newParentEntryContentValues = new ContentValues();
        newParentEntryContentValues.put("uuid", newParentEntry.getUuid());
        newParentEntryContentValues
            .put("lastSavedEventSequenceNumber", newParentEntry.getLastSavedEventSequenceNumber());
        newParentEntryContentValues.put("childrenCount", newParentEntry.getChildrenCount() + 1);
        newParentEntryContentValues.put(
            "childrenDuration", newParentEntry.getChildrenDuration() + childEntry.getDuration());
        sqlBundleList.add(new sqlBundle("viewModelEntry", newParentEntryContentValues));
      }
    }
    return sqlBundleList;

  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    Log.d(TAG, "executeSqlBundles called.");
    db.beginTransaction();
    //Todo: use conflict rollback to rollback transaction on conflict?
    try {
      for (sqlBundle sqlBundle : sqlBundleList) {
        Log.d(TAG, "executing sqlBundle " + sqlBundle.toString());
        switch (sqlBundle.getTable()) {
          case "events":
            Log.d(TAG, "case events");
            db.insertOrThrow("events", null, sqlBundle.getContentValues());
            break;
          case "aggregates":
            Log.d(TAG, "case aggregates");
            db.insertWithOnConflict(sqlBundle.getTable(), null, sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelEntry":
            Log.d(TAG, "case viewModelEntry");
            if (sqlBundle.getContentValues().get("lastSavedEventSequenceNumber").toString()
                .equals("0")) {
              db.insertOrThrow(
                  "viewModelEntry",
                  null,
                  sqlBundle.getContentValues());
            } else {
              db.updateWithOnConflict(
                  "viewModelEntry",
                  sqlBundle.getContentValues(),
                  "uuid = ?",
                  new String[]{sqlBundle.getContentValues().get("uuid").toString()},
                  SQLiteDatabase.CONFLICT_REPLACE);
            }
            break;
          default:
            Log.d(TAG, "Error: sqlBundle not handled");
            break;
        }
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing insert SQL: " + e.toString());
    } finally {
      db.endTransaction();
    }
  }


  private String jsonOf(Event event) {
    Gson gson = new Gson();
    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
        return gson.toJson(event, EntryCreatedEvent.class);
      case "EntryTitleChangedEvent":
        return gson.toJson(event, EntryTitleChangedEvent.class);
      case "EntryDescriptionChangedEvent":
        return gson.toJson(event, EntryDescriptionChangedEvent.class);
      case "EntryDurationChangedEvent":
        return gson.toJson(event, EntryDurationChangedEvent.class);
      case "EntryParentChangedEvent":
        return gson.toJson(event, EntryParentChangedEvent.class);
      default:
        return "Event class not found.";
    }
  }


  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // For initial development/testing, upgrade policy is
    // to simply to discard the data and start over
    db.execSQL("");
    onCreate(db);
  }


  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }

  public int selectLastSavedEventSequenceNumber(String aggregateId) {
    int lastSavedEventSequenceNumber = -1;

    String SELECT_MAX_VERSION_QUERY =
        "SELECT lastSavedEventSequenceNumber FROM aggregates WHERE aggregateId = ?";

    Cursor cursor = db.rawQuery(SELECT_MAX_VERSION_QUERY, new String[]{aggregateId});
    try {
      if (cursor.moveToFirst()) {
        lastSavedEventSequenceNumber =
            cursor.getInt(cursor.getColumnIndex("lastSavedEventSequenceNumber"));
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get lastSavedEventSequenceNumber from aggregates");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return lastSavedEventSequenceNumber;
  }

  public ViewModelEntry viewModelEntryFor(String entryId) {

    String SELECT_VIEWMODELENTRY_QUERY = "SELECT * FROM viewModelEntry WHERE uuid = ?";

    Cursor cursor = db.rawQuery(SELECT_VIEWMODELENTRY_QUERY, new String[]{entryId});
    ViewModelEntry viewModelEntry =
        new ViewModelEntry("error", "error", "error", "error", "error", 0, 0, 0, -9);
    try {
      if (cursor.moveToFirst()) {
        String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
        String ownerUuid = cursor.getString(cursor.getColumnIndex("ownerUuid"));
        String parentUuid = cursor.getString(cursor.getColumnIndex("parentUuid"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        int duration = cursor.getInt(cursor.getColumnIndex("duration"));
        int childrenCount = cursor.getInt(cursor.getColumnIndex("childrenCount"));
        int childrenDuration = cursor.getInt(cursor.getColumnIndex("childrenDuration"));
        int lastSavedEventSequenceNumber =
            cursor.getInt(cursor.getColumnIndex("lastSavedEventSequenceNumber"));
        viewModelEntry =
            new ViewModelEntry(
                ownerUuid,
                parentUuid,
                uuid,
                title,
                description,
                duration,
                childrenCount,
                childrenDuration,
                lastSavedEventSequenceNumber);
        return viewModelEntry;
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get viewModelEntry");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return viewModelEntry;
  }

  public List<String> getAllDirectChildrenIdsForParent(String parentUuid) {
    String SELECT_CHILDRENIDS_QUERY = "SELECT uuid FROM viewModelEntry WHERE parentUuid = ?";

    Cursor cursor = db.rawQuery(SELECT_CHILDRENIDS_QUERY, new String[]{parentUuid});
    List<String> childrenIdList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
          childrenIdList.add(uuid);
        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get children ids for parentUuid");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return childrenIdList;
  }

  public EntryCreatedEvent getEntryCreatedEvent(String uuid) {

    String SELECT_ENTRYCREATEDEVENT_QUERY =
        "SELECT data FROM events WHERE aggregateId = ? AND eventSequenceNumber = 0";

    Cursor cursor = db.rawQuery(SELECT_ENTRYCREATEDEVENT_QUERY, new String[]{uuid});
    EntryCreatedEvent entryCreatedEvent =
        EntryCreatedEvent.Create(DateTime.Create("now"), "", "", "", -1);
    try {
      if (cursor.moveToFirst()) {
        String dataJson = cursor.getString(cursor.getColumnIndex("data"));
        Gson gson = new Gson();
        entryCreatedEvent = gson.fromJson(dataJson, EntryCreatedEvent.class);
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get entryCreatedEvent from aggregates");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return entryCreatedEvent;
  }

  public List<Event> getEventsFor(String entryId) {

    String SELECT_EVENTS_QUERY = "SELECT type, data FROM events WHERE aggregateId = ?";

    Cursor cursor = db.rawQuery(SELECT_EVENTS_QUERY, new String[]{entryId});
    List<Event> eventList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String type = cursor.getString(cursor.getColumnIndex("type"));
          String dataJson = cursor.getString(cursor.getColumnIndex("data"));
          Gson gson = new Gson();
          switch (type) {
            case "EntryCreatedEvent":
              eventList.add(gson.fromJson(dataJson, EntryCreatedEvent.class));
              break;
            case "EntryTitleChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryTitleChangedEvent.class));
              break;
            case "EntryDescriptionChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryDescriptionChangedEvent.class));
              break;
            case "EntryDurationChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryDurationChangedEvent.class));
              break;
            default:
              break;
          }
        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get events from EventStore");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return eventList;
  }
}
