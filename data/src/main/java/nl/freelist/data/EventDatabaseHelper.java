package nl.freelist.data;

import static java.lang.System.exit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.dto.ViewModelAppointment;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.gson.Converters;
import nl.freelist.domain.aggregates.Person;
import nl.freelist.domain.aggregates.scheduler.Entry;
import nl.freelist.domain.aggregates.scheduler.Scheduler;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.person.PersonCreatedEvent;
import nl.freelist.domain.events.scheduler.SchedulerCreatedEvent;
import nl.freelist.domain.events.scheduler.calendar.EntryNotScheduledEvent;
import nl.freelist.domain.events.scheduler.calendar.EntryScheduledEvent;
import nl.freelist.domain.events.scheduler.entry.EntryChildCountChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryChildDurationChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryCreatedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryParentChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.scheduler.entry.EntryTitleChangedEvent;
import nl.freelist.domain.valueObjects.Id;
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
          "CREATE TABLE IF NOT EXISTS viewModelEntries (\n"
              + "   `uuid` TEXT NOT NULL\n"
              + "   , `parentUuid` TEXT NOT NULL\n"
              + "   , `startDate` TEXT NOT NULL\n"
              + "   , `type` TEXT\n"
              + "   , `json` TEXT\n"
              + "   , PRIMARY KEY(`uuid`))");
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing onCreate");
    } finally {
      db.endTransaction();
    }
  }

  public List<sqlBundle> getInitialQueriesForEvent( // Todo: remove this whole function
      String aggregateIdType, int eventSequenceNumberForQuery, Event event) throws Exception {
    Log.d(TAG, "getInitialQueriesForEvent called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();
    switch (aggregateIdType) {
      case "entry":
        sqlBundleList.add(saveToEventsTable(event, eventSequenceNumberForQuery));
        break;
      case "scheduler":
        sqlBundleList.addAll(
            getSchedulerQueriesForEvent(aggregateIdType, eventSequenceNumberForQuery, event));
        break;
      default:
        Log.e(TAG, "type of aggregate not recognized!");
        break;
    }
    return sqlBundleList;
  }

  @Nullable
  private List<sqlBundle> getSchedulerQueriesForEvent( // Todo: refactor below like with Entry
      String aggregateIdType, int eventSequenceNumberForQuery, Event event) throws Exception {
    Log.d(TAG, "getResourceQueriesForEvent called.");
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    Id personId = null;

    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
      case "EntryNotesChangedEvent":
      case "EntryDurationChangedEvent":
      case "EntryParentChangedEvent":
      case "EntryTitleChangedEvent":
      case "EntryScheduledEvent":
        return null; // Todo: what entry events affect the calendar and should thus also be applied
      // to the resource? ie duration changed
      case "PersonCreatedEvent":
        PersonCreatedEvent personCreatedEvent = (PersonCreatedEvent) event;
        personId = personCreatedEvent.getAggregateId();
        break;
      default:
        Log.e(TAG, "Unrecognized: " + event.getClass().getSimpleName());
        exit(-1);
        break;
    }

    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", personId.toString());
    aggregateContentValues.put("type", aggregateIdType);
    aggregateContentValues.put("lastSavedEventSequenceNumber", eventSequenceNumberForQuery);
    sqlBundleList.add(new sqlBundle("aggregates", aggregateContentValues));

    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", personId.toString());
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", eventSequenceNumberForQuery);
    sqlBundleList.add(new sqlBundle("events", eventContentValues));

    return sqlBundleList;
  }

  public Scheduler getSchedulerWithSavedEventsById(Id personId) {
    Scheduler scheduler = Scheduler.Create(personId);
    List<Event> eventList = getEventsFor(personId);
    if (eventList.size() > 0) {
      scheduler.applyEvents(eventList);
    } else {
      SchedulerCreatedEvent schedulerCreatedEvent =
          SchedulerCreatedEvent.Create(OffsetDateTime.now(ZoneOffset.UTC), personId);
      scheduler.applyEvent(schedulerCreatedEvent);
      try {
        insert(scheduler);
      } catch (Exception e) {
        e.printStackTrace();
        exit(-9);
      }
    }
    return scheduler;
  }

  public int selectLastSavedEventSequenceNumber(Id aggregateId) {
    int lastSavedEventSequenceNumber = -1;

    String SELECT_MAX_VERSION_QUERY =
        "SELECT lastSavedEventSequenceNumber FROM aggregates WHERE aggregateId = ?";

    Cursor cursor = db.rawQuery(SELECT_MAX_VERSION_QUERY, new String[]{aggregateId.toString()});
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

  public Person getPersonWithSavedEventsById(Id personId) {
    Person person = Person.Create();
    List<Event> eventList = getEventsFor(personId);
    person.applyEvents(eventList);
    return person;
  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    Log.d(TAG, "executeSqlBundles called.");
    Log.d(TAG, "sqlBundle length : " + sqlBundleList.size());
    try {
      db.beginTransaction();
      for (sqlBundle sqlBundle : sqlBundleList) {
        Log.d(
            TAG,
            "executing sqlBundle in table "
                + sqlBundle.getTable()
                + ": "
                + sqlBundle.getContentValues().toString());
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
          case "viewModelAppointments":
            Log.d(TAG, "case table viewModelAppointments");
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelEntries":
            Log.d(TAG, "case table viewModelEntries");
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
    Gson gson = Converters.registerAll(new GsonBuilder()).create();
    switch (event.getClass().getSimpleName()) {
      case "SchedulerCreatedEvent":
        return gson.toJson(event, SchedulerCreatedEvent.class);
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
      case "EntryNotScheduledEvent":
        return gson.toJson(event, EntryNotScheduledEvent.class);
      case "PersonCreatedEvent":
        return gson.toJson(event, PersonCreatedEvent.class);
      case "EntryStartDateTimeChangedEvent":
        return gson.toJson(event, EntryStartDateTimeChangedEvent.class);
      case "EntryEndDateTimeChangedEvent":
        return gson.toJson(event, EntryEndDateTimeChangedEvent.class);
      case "EntryChildCountChangedEvent":
        return gson.toJson(event, EntryChildCountChangedEvent.class);
      case "EntryChildDurationChangedEvent":
        return gson.toJson(event, EntryChildDurationChangedEvent.class);
      case "EntryPreferredDayConstraintsChangedEvent":
        return gson.toJson(event, EntryPreferredDayConstraintsChangedEvent.class);
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

  public ViewModelEntry viewModelEntryFor(String entryId) {
    String SELECT_VIEWMODELENTRY_QUERY = "SELECT json FROM viewModelEntries WHERE uuid = ?";

    Cursor cursor = db.rawQuery(SELECT_VIEWMODELENTRY_QUERY, new String[]{entryId});
    String jsonBlob;
    try {
      if (cursor.moveToFirst()) {
        jsonBlob = cursor.getString(cursor.getColumnIndex("json"));
        Gson gson = Converters.registerAll(new GsonBuilder()).create();
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

  public List<ViewModelEntry> getViewModelEntries(String personId) {
    String SELECT_ALL_VIEWMODELENTRIES = "SELECT json FROM viewModelEntries";

    Cursor cursor = db.rawQuery(SELECT_ALL_VIEWMODELENTRIES, null);
    List<String> jsonList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String json = cursor.getString(cursor.getColumnIndex("json"));
          jsonList.add(json);
        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get all viewModelEntries");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }
    List<ViewModelEntry> viewModelEntryList = new ArrayList<>();
    Gson gson = Converters.registerAll(new GsonBuilder()).create();
    ViewModelEntry viewModelEntry;
    for (String json : jsonList) {
      viewModelEntry = gson.fromJson(json, ViewModelEntry.class);
      viewModelEntryList.add(viewModelEntry);
    }
    return viewModelEntryList;
  }

  public List<Event> getEventsFor(Id uuid) {

    String SELECT_EVENTS_QUERY =
        "SELECT type, data FROM events WHERE aggregateId = ? ORDER BY eventSequenceNumber ASC";

    Cursor cursor = db.rawQuery(SELECT_EVENTS_QUERY, new String[]{uuid.toString()});
    List<Event> eventList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String type = cursor.getString(cursor.getColumnIndex("type"));
          String dataJson = cursor.getString(cursor.getColumnIndex("data"));
          Gson gson = Converters.registerAll(new GsonBuilder()).create();
          switch (type) {
            case "SchedulerCreatedEvent":
              eventList.add(gson.fromJson(dataJson, SchedulerCreatedEvent.class));
              break;
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
            case "EntryNotScheduledEvent":
              eventList.add(gson.fromJson(dataJson, EntryNotScheduledEvent.class));
              break;
            case "PersonCreatedEvent":
              eventList.add(gson.fromJson(dataJson, PersonCreatedEvent.class));
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
            case "EntryPreferredDayConstraintsChangedEvent":
              eventList.add(
                  gson.fromJson(dataJson, EntryPreferredDayConstraintsChangedEvent.class));
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
      db.execSQL("DELETE FROM events\n");
      db.execSQL("DELETE FROM aggregates\n");
      db.execSQL("DELETE FROM viewModelEntries \n");
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing deleteAllEntriesFromRepository");
    } finally {
      db.endTransaction();
    }
  }

  private sqlBundle saveSchedulerToAggregatesTable(
      Id aggregateId, int eventSequenceNumberForQuery) {
    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", aggregateId.toString());
    aggregateContentValues.put("type", "Scheduler");
    aggregateContentValues.put("lastSavedEventSequenceNumber", eventSequenceNumberForQuery);
    return new sqlBundle("aggregates", aggregateContentValues);
  }

  private sqlBundle saveToEventsTable(Event event, int eventSequenceNumberForQuery) {
    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", event.getAggregateId().toString());
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", eventSequenceNumberForQuery);
    return new sqlBundle("events", eventContentValues);
  }

  private List<sqlBundle> getQueriesForSaving(Scheduler scheduler) throws Exception {
    List<sqlBundle> sqlBundleListInitial = new ArrayList<>();

    int lastSavedEventSequenceNumber = selectLastSavedEventSequenceNumber(scheduler.getPersonId());
    Log.d(TAG, "Scheduler lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    List<Event> newEventsToSave =
        scheduler.getListOfEventsWithSequenceHigherThan(lastSavedEventSequenceNumber);
    Log.d(TAG, "newEventsToSave list with size " + newEventsToSave.size() + " retrieved.");

    int eventSequenceNumberForQuery = lastSavedEventSequenceNumber;
    for (Event event : newEventsToSave) {
      eventSequenceNumberForQuery += 1;
      sqlBundleListInitial.add(saveToEventsTable(event, eventSequenceNumberForQuery));
    }
    if (sqlBundleListInitial.size() > 0) { // check only initial query bundle
      sqlBundleListInitial.addAll(
          getViewModelEntryQueriesFrom(scheduler, eventSequenceNumberForQuery));
      //      sqlBundleListInitial.add(getViewModelAppointmentQueriesFrom(scheduler));
      sqlBundleListInitial.add(
          saveSchedulerToAggregatesTable(
              scheduler.getPersonId(), scheduler.getLastAppliedEventSequenceNumber()));
    }
    return sqlBundleListInitial;
  }

  private List<sqlBundle> getViewModelEntryQueriesFrom(
      Scheduler scheduler, int eventSequenceNumberForQuery) {
    List<Entry> entryList = scheduler.getEntries();
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    for (Entry entry : entryList) {
      ViewModelEntry viewModelEntry;
      viewModelEntry = getViewModelEntryFrom(entry, eventSequenceNumberForQuery);
      ContentValues viewModelEntryContentValues = new ContentValues();
      viewModelEntryContentValues.put("uuid", entry.getEntryId().toString());
      viewModelEntryContentValues.put("parentUuid", entry.getParentEntryId().toString());
      viewModelEntryContentValues.put("json", jsonOf(viewModelEntry));
      sqlBundleList.add(new sqlBundle("viewModelEntries", viewModelEntryContentValues));
    }
    return sqlBundleList;
  }

  public void insert(Scheduler scheduler) throws Exception {
    Log.i(TAG, "Repository insert called with entry " + scheduler.getPersonId());
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    sqlBundleList.addAll(getQueriesForSaving(scheduler));
    executeSqlBundles(sqlBundleList);
  }

  private ViewModelEntry getViewModelEntryFrom(
      Entry entry, int lastAppliedSchedulerSequenceNumber) {
    ViewModelEntry viewModelEntry;
    viewModelEntry =
        new ViewModelEntry(
            entry.getPersonId(),
            entry.getParentEntryId(),
            entry.getEntryId(),
            entry.getTitle(),
            entry.getStartAtOrAfterDateTime(),
            entry.getDuration(),
            entry.getFinishAtOrBeforeDateTime(),
            entry.getImpossibleDaysConstraints(),
            entry.getNotes(),
            entry.getChildCount(),
            entry.getChildDuration(),
            entry.getScheduledStartDateTime(),
            entry.getScheduledEndDateTime(),
            lastAppliedSchedulerSequenceNumber);
    return viewModelEntry;
  }

  private String jsonOf(ViewModelEntry viewModelEntry) {
    Gson gson = Converters.registerAll(new GsonBuilder()).create();
    return gson.toJson(viewModelEntry);
  }

  private String jsonOf(ViewModelAppointment viewModelAppointment) {
    Gson gson = Converters.registerAll(new GsonBuilder()).create();
    return gson.toJson(viewModelAppointment);
  }

}
