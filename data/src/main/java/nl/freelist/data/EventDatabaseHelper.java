package nl.freelist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.dto.CalendarEntry;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.gson.Converters;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.TimeHelper;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryChildCountChangedEvent;
import nl.freelist.domain.events.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.ResourceCreatedEvent;
import org.jetbrains.annotations.Nullable;

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
              + "   , `json` TEXT\n"
              + "   , PRIMARY KEY(`uuid`))");
      db.execSQL(
          "CREATE TABLE IF NOT EXISTS viewModelCalendar (\n"
              + "   `entryUuid` TEXT NOT NULL\n"
              + "   , `title` TEXT\n"
              + "   , `date` TEXT NOT NULL\n"
              + "   , `time` TEXT\n"
              + "   , `duration` INTEGER NOT NULL DEFAULT 0"
              + "   , `isDone` INTEGER NOT NULL DEFAULT 0"
              + "   , `lastSavedEventSequenceNumber` INTEGER)"
      );
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing onCreate");
    } finally {
      db.endTransaction();
    }
  }


  public List<sqlBundle> getInitialQueriesForEvent( //Todo: remove this whole function
      String aggregateIdType,
      int eventSequenceNumberForQuery,
      Event event)
      throws Exception {
    Log.d(TAG, "getInitialQueriesForEvent called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();
    switch (aggregateIdType) {
      case "entry":
        sqlBundleList.add(saveToEventsTable(event, eventSequenceNumberForQuery));
        break;
      case "resource":
        sqlBundleList.addAll(
            getResourceQueriesForEvent(
                aggregateIdType,
                eventSequenceNumberForQuery,
                event
            )
        );
        break;
      default:
        Log.e(TAG, "type of aggregate not recognized!");
        break;
    }
    return sqlBundleList;
  }

  @Nullable
  private List<sqlBundle> getResourceQueriesForEvent( //Todo: refactor below like with Entry
      String aggregateIdType,
      int eventSequenceNumberForQuery,
      Event event)
      throws Exception {
    Log.d(TAG, "getResourceQueriesForEvent called.");
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    String resourceId = "";

    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
      case "EntryNotesChangedEvent":
      case "EntryDurationChangedEvent":
      case "EntryParentChangedEvent":
      case "EntryTitleChangedEvent":
        return null; //Todo: what entry events affect the calendar and should thus also be applied to the resource? ie duration changed
      case "ResourceCreatedEvent":
        ResourceCreatedEvent resourceCreatedEvent = (ResourceCreatedEvent) event;
        resourceId = resourceCreatedEvent.getAggregateId();
        break;
      case "EntryScheduledEvent":
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        resourceId = entryScheduledEvent.getResourceUuid();
        Entry entry = getEntryWithSavedEventsById(entryScheduledEvent.getAggregateId());

        ContentValues calendarContentValues2 = new ContentValues();
        calendarContentValues2.put("entryUuid", entry.getUuid().toString());
        calendarContentValues2.put("title", entry.getTitle());
        calendarContentValues2.put("date", entryScheduledEvent.getCalendar()
            .getEntryLastScheduledDateTimeRange()
            .getStartDateTime()
            .format(DateTimeFormatter.ISO_DATE));
        calendarContentValues2.put("time", entryScheduledEvent.getCalendar()
            .getEntryLastScheduledDateTimeRange()
            .getStartDateTime()
            .format(DateTimeFormatter.ISO_LOCAL_TIME));
        calendarContentValues2.put("duration", entryScheduledEvent.getCalendar()
            .getEntryLastScheduledDateTimeRange()
            .getDuration());
        calendarContentValues2.put("isDone", 0);
        calendarContentValues2
            .put("lastSavedEventSequenceNumber", eventSequenceNumberForQuery + 1);

        sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues2));
        break;
      default:
        Log.e(TAG, "Unrecognized: " + event.getClass().getSimpleName());
        resourceId = "error unrecognized event class";
        break;
    }

    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", resourceId);
    aggregateContentValues.put("type", aggregateIdType);
    aggregateContentValues.put("lastSavedEventSequenceNumber", eventSequenceNumberForQuery);
    sqlBundleList.add(new sqlBundle("aggregates", aggregateContentValues));

    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", resourceId);
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", eventSequenceNumberForQuery);
    sqlBundleList.add(new sqlBundle("events", eventContentValues));

    return sqlBundleList;
  }

  public Entry getEntryWithSavedEventsById(String uuid) {
    Entry entry = new Entry();
    List<Event> eventList = getEventsFor(uuid);
    entry.applyEvents(eventList);
    return entry;
  }

  public Resource getResourceWithSavedEventsById(String uuid) {
    Resource resource = Resource.Create();
    List<Event> eventList = getEventsFor(uuid);
    resource.applyEvents(eventList);
    return resource;
  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    Log.d(TAG, "executeSqlBundles called.");
    Log.d(TAG, "sqlBundle length : " + sqlBundleList.size());
    try {
      db.beginTransaction();
      for (sqlBundle sqlBundle : sqlBundleList) {
        Log.d(TAG, "executing sqlBundle in table " + sqlBundle.getTable() + ": " + sqlBundle
            .getContentValues().toString());
        switch (sqlBundle.getTable()) {
          case "events":
            Log.d(TAG, "case table events");
            db.insertOrThrow("events", null, sqlBundle.getContentValues());
            break;
          case "aggregates":
            Log.d(TAG, "case table aggregates");
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelCalendar":
            Log.d(TAG, "case table viewModelCalendar");
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelEntry":
            Log.d(TAG, "case table viewModelEntry");
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          default:
            Log.d(TAG, "Error: sqlBundle not handled since table not recognized");
            throw new Exception("Error in sqlBundle handling, table in sqlBundle not recognized.");
        }
      }
      db.setTransactionSuccessful();
      db.endTransaction();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing insert SQL: " + e.toString());

    }
  }

  private String jsonOf(Event event) {
    Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
        return gson.toJson(event, EntryCreatedEvent.class);
      case "EntryTitleChangedEvent":
        return gson.toJson(event, EntryTitleChangedEvent.class);
      case "EntryNotesChangedEvent":
        return gson.toJson(event, EntryNotesChangedEvent.class);
      case "EntryDurationChangedEvent":
        return gson.toJson(event, EntryDurationChangedEvent.class);
      case "EntryParentChangedEvent":
        return gson.toJson(event, EntryParentChangedEvent.class);
      case "EntryScheduledEvent":
        return gson.toJson(event, EntryScheduledEvent.class);
      case "ResourceCreatedEvent":
        return gson.toJson(event, ResourceCreatedEvent.class);
      case "EntryStartDateTimeChangedEvent":
        return gson.toJson(event, EntryStartDateTimeChangedEvent.class);
      case "EntryEndDateTimeChangedEvent":
        return gson.toJson(event, EntryEndDateTimeChangedEvent.class);
      case "EntryChildCountChangedEvent":
        return gson.toJson(event, EntryChildCountChangedEvent.class);
      case "EntryChildDurationChangedEvent":
        return gson.toJson(event, EntryChildDurationChangedEvent.class);
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

  public List<CalendarEntry> getAllCalendarEntries() {
    List<CalendarEntry> calendarEntryList = new ArrayList<>();

    String SELECT_CALENDAR_ENTRIES_QUERY =
        "SELECT * FROM viewModelCalendar";

    Cursor cursor = db.rawQuery(SELECT_CALENDAR_ENTRIES_QUERY, new String[]{});

    try {
      if (cursor.moveToFirst()) {
        do {
          String entryUuid = cursor.getString(cursor.getColumnIndex("entryUuid"));
          String title = cursor.getString(cursor.getColumnIndex("title"));
          String date = cursor.getString(cursor.getColumnIndex("date"));
          String time = cursor.getString(cursor.getColumnIndex("time"));
          int duration = cursor.getInt(cursor.getColumnIndex("duration"));
          int isDone = cursor.getInt(cursor.getColumnIndex("isDone"));
          int lastSavedEventSequenceNumber = cursor.getInt(cursor.getColumnIndex(
              "lastSavedEventSequenceNumber"));

          CalendarEntry calendarEntry =
              new CalendarEntry(entryUuid, title, Constants.CALENDAR_ENTRY_TODO_VIEW_TYPE, date,
                  time,
                  TimeHelper.getDurationStringFrom(duration));
          calendarEntryList.add(calendarEntry);
        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get ancestor ids for uuid");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    return calendarEntryList;
  }

  public ViewModelEntry viewModelEntryFor(String entryId) {
    String SELECT_VIEWMODELENTRY_QUERY = "SELECT json FROM viewModelEntry WHERE uuid = ?";

    Cursor cursor = db.rawQuery(SELECT_VIEWMODELENTRY_QUERY, new String[]{entryId});
    String jsonBlob;
    try {
      if (cursor.moveToFirst()) {
        jsonBlob = cursor.getString(cursor.getColumnIndex("json"));
        Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
        ViewModelEntry viewModelEntry;
        viewModelEntry = gson.fromJson(jsonBlob, ViewModelEntry.class);
        return viewModelEntry;
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get viewModelEntry");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    return null;
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


  public List<Event> getEventsFor(String uuid) {

    String SELECT_EVENTS_QUERY = "SELECT type, data FROM events WHERE aggregateId = ? ORDER BY occurredDateTime ASC";

    Cursor cursor = db.rawQuery(SELECT_EVENTS_QUERY, new String[]{uuid});
    List<Event> eventList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String type = cursor.getString(cursor.getColumnIndex("type"));
          String dataJson = cursor.getString(cursor.getColumnIndex("data"));
          Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
          switch (type) {
            case "EntryCreatedEvent":
              eventList.add(gson.fromJson(dataJson, EntryCreatedEvent.class));
              break;
            case "EntryTitleChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryTitleChangedEvent.class));
              break;
            case "EntryNotesChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryNotesChangedEvent.class));
              break;
            case "EntryDurationChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryDurationChangedEvent.class));
              break;
            case "EntryParentChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryParentChangedEvent.class));
              break;
            case "EntryScheduledEvent":
              eventList.add(gson.fromJson(dataJson, EntryScheduledEvent.class));
              break;
            case "ResourceCreatedEvent":
              eventList.add(gson.fromJson(dataJson, ResourceCreatedEvent.class));
              break;
            case "EntryStartDateTimeChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryStartDateTimeChangedEvent.class));
              break;
            case "EntryEndDateTimeChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryEndDateTimeChangedEvent.class));
              break;
            case "EntryChildCountChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryChildCountChangedEvent.class));
              break;
            case "EntryChildDurationChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryChildDurationChangedEvent.class));
              break;
            default:
              throw new Exception("ERROR: event gson not recognized!");
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

  public void deleteAllEntriesFromRepository() {
    Log.d(TAG, "deleteAllEntriesFromRepository called.");
    db.beginTransaction();
    try {
      db.execSQL(
          "DELETE FROM events\n");
      db.execSQL(
          "DELETE FROM aggregates WHERE type = \"Entry\"\n");
      db.execSQL(
          "DELETE FROM viewModelEntry \n");
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing deleteAllEntriesFromRepository");
    } finally {
      db.endTransaction();
    }
  }

  private sqlBundle saveEntryToAggregatesTable(String aggregateId,
      int eventSequenceNumberForQuery) {
    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", aggregateId);
    aggregateContentValues.put("type", "Entry");
    aggregateContentValues.put("lastSavedEventSequenceNumber", eventSequenceNumberForQuery);
    return new sqlBundle("aggregates", aggregateContentValues);
  }

  private sqlBundle saveToEventsTable(Event event, int eventSequenceNumberForQuery) {
    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", event.getAggregateId());
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", eventSequenceNumberForQuery);
    return new sqlBundle("events", eventContentValues);
  }


  public List<sqlBundle> getAdditionalQueriesForEvent(Event event, Entry entry,
      int eventSequenceNumber)
      throws Exception {
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    String parentUid = entry.getParentUuid().toString();
    EntryChildDurationChangedEvent entryChildDurationChangedEvent;

    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
        if (parentUid != null) { //makes recursive calls
          Entry parentEntry = getEntryWithSavedEventsById(parentUid);
          if (parentEntry.getParentUuid() != null) { //because parentEntry could be the root
            EntryChildCountChangedEvent entryChildCountChangedEvent = EntryChildCountChangedEvent
                .Create(
                    event.getOccurredDateTime(),
                    parentUid,
                    1
                );
            parentEntry.applyEvent(entryChildCountChangedEvent);
            sqlBundleList.addAll(getQueriesForSaving(parentEntry));
          }
        }
        break;
      case "EntryParentChangedEvent":
        //Todo: add sqlBundles to update all old parents with childEntryChangedParentEvent
        //Todo: add sqlBundles to update all new parents with newChildEntryAttachedEvent
        break;
      case "EntryNotesChangedEvent":
      case "EntryScheduledEvent"://Calendar update is done when Event is applied to Resource
      case "EntryTitleChangedEvent":
      case "EntryStartDateTimeChangedEvent":
      case "EntryEndDateTimeChangedEvent":
      case "ResourceCreatedEvent":
      case "EntryChildCountChangedEvent":
        //Todo: implement
        break;
      case "EntryChildDurationChangedEvent":
        entryChildDurationChangedEvent = (EntryChildDurationChangedEvent) event;
        if (parentUid != null) { //makes recursive calls
          Entry parentEntry = getEntryWithSavedEventsById(parentUid);
          long durationDelta = entryChildDurationChangedEvent.getDurationDelta();
          if (parentEntry.getParentUuid() != null) { //because parentEntry could be the root
            EntryChildDurationChangedEvent entryChildDurationChangedEvent2 = EntryChildDurationChangedEvent
                .Create(
                    event.getOccurredDateTime(),
                    parentUid,
                    durationDelta
                );
            parentEntry.applyEvent(entryChildDurationChangedEvent2);
            sqlBundleList.addAll(getQueriesForSaving(parentEntry));
          }
        }
        break;
      case "EntryDurationChangedEvent":
        EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
        if (parentUid != null) { //makes recursive calls
          Entry parentEntry = getEntryWithSavedEventsById(parentUid);
          EntryDurationChangedEvent previousDurationChangedEvent = (EntryDurationChangedEvent) entry
              .getPreviousOf(entryDurationChangedEvent, eventSequenceNumber);
          long durationDelta;
          if (previousDurationChangedEvent != null) {
            durationDelta =
                entryDurationChangedEvent.getDurationAfter() - previousDurationChangedEvent
                    .getDurationAfter();
          } else {
            durationDelta = entryDurationChangedEvent.getDurationAfter();
          }
          if (parentEntry.getParentUuid() != null) { //because parentEntry could be the root
            entryChildDurationChangedEvent = EntryChildDurationChangedEvent
                .Create(
                    event.getOccurredDateTime(),
                    parentUid,
                    durationDelta
                );
            parentEntry.applyEvent(entryChildDurationChangedEvent);
            sqlBundleList.addAll(getQueriesForSaving(parentEntry));
          }
        }
        break;
      default:
        Log.w(TAG, "Unrecognized: " + event.getClass().getSimpleName());
        break;

    }
    return sqlBundleList;
  }


  private List<sqlBundle> getQueriesForSaving(Entry entry)
      throws Exception {
    List<sqlBundle> sqlBundleListInitial = new ArrayList<>();
    List<sqlBundle> sqlBundleListAdditional = new ArrayList<>();

    int lastSavedEventSequenceNumber =
        selectLastSavedEventSequenceNumber(entry.getUuid().toString());
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    List<Event> newEventsToSave =
        entry.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");

    int eventSequenceNumberForQuery = lastSavedEventSequenceNumber;
    for (Event event : newEventsToSave) {
      eventSequenceNumberForQuery += 1;
      sqlBundleListInitial.add(saveToEventsTable(event, eventSequenceNumberForQuery));
      sqlBundleListAdditional.addAll(
          getAdditionalQueriesForEvent(event, entry, eventSequenceNumberForQuery)
          //An Event should know which other Events it should trigger within the same transaction
          //For example, when a subList changes duration or gets removed from a parent, the parent should be updated with an event
      );
    }
    if (sqlBundleListInitial.size() > 0) { // check only initial query bundle
      sqlBundleListInitial.add(getViewModelQueriesFrom(entry));
      sqlBundleListInitial.add(saveEntryToAggregatesTable(entry.getUuid().toString(),
          entry.getLastAppliedEventSequenceNumber()));
    }
    sqlBundleListInitial.addAll(sqlBundleListAdditional);
    return sqlBundleListInitial;
  }

  private sqlBundle getViewModelQueriesFrom(Entry entry) {
    ViewModelEntry viewModelEntry;
    viewModelEntry = getViewModelEntryFrom(entry);
    ContentValues viewModelEntryContentValues = new ContentValues();
    viewModelEntryContentValues.put("uuid", entry.getUuid().toString());
    viewModelEntryContentValues.put("parentUuid", entry.getParentUuid().toString());
    viewModelEntryContentValues.put("json", jsonOf(viewModelEntry));
    return new sqlBundle("viewModelEntry", viewModelEntryContentValues);
  }


  public void insert(Entry entry)
      throws Exception {
    Log.i(TAG, "Repository insert called with entry " + entry.getUuid());
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    sqlBundleList.addAll(getQueriesForSaving(entry));
    executeSqlBundles(sqlBundleList);
  }

  private ViewModelEntry getViewModelEntryFrom(Entry entry) {
    ViewModelEntry viewModelEntry;
    viewModelEntry = new ViewModelEntry(
        entry.getOwnerUuid().toString(),
        entry.getParentUuid().toString(),
        entry.getUuid().toString(),
        entry.getTitle(),
        entry.getStartDateTime(),
        entry.getDuration(),
        entry.getEndDateTime(),
        entry.getNotes(),
        entry.getChildCount(),
        entry.getChildDuration(),
        entry.getLastAppliedEventSequenceNumber()
    );
    return viewModelEntry;
  }

  private String jsonOf(ViewModelEntry viewModelEntry) {
    Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
    return gson.toJson(viewModelEntry);
  }
}