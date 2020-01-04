package nl.freelist.data;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.dto.CalendarEntry;
import nl.freelist.data.dto.ViewModelEntry;
import nl.freelist.data.gson.Converters;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.DurationHelper;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.ResourceCreatedEvent;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

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
      db.execSQL(
          "CREATE TABLE IF NOT EXISTS viewModelEntry2 (\n"
              + "   `uuid` TEXT NOT NULL\n"
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

  private List<sqlBundle> modifyChildrenCountAndDurationIncludingAncestorsFor(
      //todo: refactor to use json viewModelEntry
      String firstParent, int childDurationDelta, int childCountDelta, String root) {
    Log.d(TAG,
        "modifyChildrenCountAndDurationIncludingAncestorsFor called with firstParent " + firstParent
            + " and root " + root);
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    List<String> oldAncestorList =
        getAncestorIdsExcludingRootForAndIncludingThisParent(firstParent, root);
    for (String oldParent : oldAncestorList) {
      ViewModelEntry oldParentEntry = viewModelEntryFor(oldParent);
      ContentValues oldParentEntryContentValues = new ContentValues();
      oldParentEntryContentValues.put("uuid", oldParentEntry.getUuid());
      oldParentEntryContentValues.put(
          "lastSavedEventSequenceNumber", oldParentEntry.getLastSavedEventSequenceNumber());
      oldParentEntryContentValues.put(
          "childrenCount", oldParentEntry.getChildrenCount() + childCountDelta);
      oldParentEntryContentValues.put(
          "childrenDuration", oldParentEntry.getChildrenDuration() + childDurationDelta);
      sqlBundleList.add(new sqlBundle("viewModelEntry", oldParentEntryContentValues));
    }
    Log.d(TAG, sqlBundleList.toString());
    return sqlBundleList;
  }

  public void tempFillViewModelCalendar() {
    Log.d(TAG, "tempFillViewModelCalendar called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();

    ContentValues calendarContentValues = new ContentValues();
    calendarContentValues.put("entryUuid", "6910aac6-e16c-4ba0-8282-4d48f1281e16");
    calendarContentValues.put("title", "title1");
    calendarContentValues.put("date", "2019-01-01");
    calendarContentValues.put("time", "");
    calendarContentValues.put("duration", 0);
    calendarContentValues.put("isDone", 0);
    calendarContentValues.put("lastSavedEventSequenceNumber", 0);

    ContentValues calendarContentValues4 = new ContentValues();
    calendarContentValues4.put("entryUuid", "6910aac6-e16c-4ba0-8282-4d48f1281e16");
    calendarContentValues4.put("title", "title2");
    calendarContentValues4.put("date", "2019-01-10");
    calendarContentValues4.put("time", "12:00");
    calendarContentValues4.put("duration", 0);
    calendarContentValues4.put("isDone", 0);
    calendarContentValues4.put("lastSavedEventSequenceNumber", 0);

    ContentValues calendarContentValues2 = new ContentValues();
    calendarContentValues2.put("entryUuid", "6910aac6-e16c-4ba0-8282-4d48f1281e16");
    calendarContentValues2.put("title", "title2");
    calendarContentValues2.put("date", "2019-01-10");
    calendarContentValues2.put("time", "11:00");
    calendarContentValues2.put("duration", 0);
    calendarContentValues2.put("isDone", 0);
    calendarContentValues2.put("lastSavedEventSequenceNumber", 0);

    ContentValues calendarContentValues3 = new ContentValues();
    calendarContentValues3.put("entryUuid", "6910aac6-e16c-4ba0-8282-4d48f1281e16");
    calendarContentValues3.put("title", "title3");
    calendarContentValues3.put("date", "2019-02-10");
    calendarContentValues3.put("time", "");
    calendarContentValues3.put("duration", 60);
    calendarContentValues3.put("isDone", 0);
    calendarContentValues3.put("lastSavedEventSequenceNumber", 0);

    sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues));
    sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues4));
    sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues2));
    sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues3));

    executeSqlBundles(sqlBundleList);
  }

  public List<sqlBundle> getQueriesForEvent( //Todo: sort out below mess...
      String aggregateIdType, int expectedLastSavedEventSequenceNumber, Event event)
      throws Exception {
    Log.d(TAG, "getQueriesForEvent called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();
    switch (aggregateIdType) {
      case "entry":
        sqlBundleList.addAll(getEntryQueriesForEvent(
            aggregateIdType,
            expectedLastSavedEventSequenceNumber,
            event
        ));
        break;
      case "resource":
        sqlBundleList.addAll(getResourceQueriesForEvent(
            aggregateIdType,
            expectedLastSavedEventSequenceNumber,
            event
        ));
        break;
      default:
        Log.e(TAG, "type of aggregate not recognized!");
        break;
    }
    return sqlBundleList;
  }

  private List<sqlBundle> getResourceQueriesForEvent(
      String aggregateIdType,
      int expectedLastSavedEventSequenceNumber,
      Event event)
      throws Exception {
    Log.d(TAG, "getResourceQueriesForEvent called.");
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    int toSaveSequenceNumber;
    String resourceId = "";

    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
      case "EntryDescriptionChangedEvent":
      case "EntryDurationChangedEvent":
      case "EntryParentChangedEvent":
      case "EntryTitleChangedEvent":
        return null; //Todo: what entry events affect the calendar and should thus also be applied to the resource? ie duration changed
      case "ResourceCreatedEvent":
        ResourceCreatedEvent resourceCreatedEvent = (ResourceCreatedEvent) event;
        toSaveSequenceNumber = resourceCreatedEvent.getEventSequenceNumber();
        resourceId = resourceCreatedEvent.getAggregateId();
        break;
      case "EntryScheduledEvent":
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        toSaveSequenceNumber = entryScheduledEvent.getResourceEventSequenceNumber();
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
            .put("lastSavedEventSequenceNumber", expectedLastSavedEventSequenceNumber + 1);

        sqlBundleList.add(new sqlBundle("viewModelCalendar", calendarContentValues2));
        break;
      default:
        Log.e(TAG, "Unrecognized: " + event.getClass().getSimpleName());
        toSaveSequenceNumber = -9;
        resourceId = "error unrecognized event class";
        break;
    }

    int lastSavedResourceSequenceNumber = toSaveSequenceNumber - 1;
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedResourceSequenceNumber);

    if (lastSavedResourceSequenceNumber != expectedLastSavedEventSequenceNumber) {
      throw new Exception("Optimistic concurrency exception");
    }

    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", resourceId);
    aggregateContentValues.put("type", aggregateIdType);
    aggregateContentValues.put("lastSavedEventSequenceNumber", lastSavedResourceSequenceNumber + 1);
    sqlBundleList.add(new sqlBundle("aggregates", aggregateContentValues));

    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", resourceId);
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", lastSavedResourceSequenceNumber + 1);
    sqlBundleList.add(new sqlBundle("events", eventContentValues));

    return sqlBundleList;
  }

  private List<sqlBundle> getEntryQueriesForEvent(
      //Todo: sort out below mess...
      String aggregateIdType, int expectedLastSavedEventSequenceNumber, Event event)
      throws Exception {
    Log.d(TAG, "getEntryQueriesForEvent called.");

    List<sqlBundle> sqlBundleList = new ArrayList<>();
    int toSaveSequenceNumber;
    String entryId;

    switch (event.getClass().getSimpleName()) {
      case "EntryCreatedEvent":
        EntryCreatedEvent entryCreatedEvent = (EntryCreatedEvent) event;

        toSaveSequenceNumber = entryCreatedEvent.getEventSequenceNumber();
        entryId = entryCreatedEvent.getAggregateId();
        Log.d(TAG, "creating initial viewModelEntry query for EntryCreatedEvent");
        sqlBundleList.addAll(
            modifyChildrenCountAndDurationIncludingAncestorsFor(
                entryCreatedEvent.getParentUuid(), 0, 1, entryCreatedEvent.getOwnerUuid()));
        break;
      case "EntryDescriptionChangedEvent":
        EntryDescriptionChangedEvent entryDescriptionChangedEvent = (EntryDescriptionChangedEvent) event;
        toSaveSequenceNumber = entryDescriptionChangedEvent.getEventSequenceNumber();
        entryId = entryDescriptionChangedEvent.getAggregateId();
        break;
      case "EntryDurationChangedEvent":
        EntryDurationChangedEvent entryDurationChangedEvent = (EntryDurationChangedEvent) event;
        toSaveSequenceNumber = entryDurationChangedEvent.getEventSequenceNumber();
        entryId = entryDurationChangedEvent.getAggregateId();
        Log.d(TAG, "creating additional viewModelEntry query for EntryDurationChangedEvent");
        ViewModelEntry changedEntry = viewModelEntryFor(entryDurationChangedEvent.getAggregateId());
        sqlBundleList.addAll(
            //Todo: Refactor to emit new SublistDurationChangedEvent + generate queries via recursive call to getEntryQueriesForEvent(Event)
            modifyChildrenCountAndDurationIncludingAncestorsFor(
                changedEntry.getParentUuid(),
                DurationHelper.getDurationSecondsDeltaFromDurationChangedEvent(
                    entryDurationChangedEvent),
                0,
                changedEntry.getOwnerUuid()));
        break;
      case "EntryParentChangedEvent":
        EntryParentChangedEvent entryParentChangedEvent = (EntryParentChangedEvent) event;
        toSaveSequenceNumber = entryParentChangedEvent.getEventSequenceNumber();
        entryId = entryParentChangedEvent.getAggregateId();
        Log.d(TAG, "creating additional viewModelEntry query for EntryParentChangedEvent");
        ViewModelEntry childEntry = viewModelEntryFor(entryParentChangedEvent.getAggregateId());
        sqlBundleList.addAll(
            //Todo: Refactor to emit new SublistAddedEvent/SublistRemovedEvent + generate queries via recursive call to getEntryQueriesForEvent(Event)
            modifyChildrenCountAndDurationIncludingAncestorsFor(
                entryParentChangedEvent.getParentBefore(),
                -childEntry.getChildrenDuration() - childEntry.getDuration(),
                -childEntry.getChildrenCount() - 1,
                childEntry.getOwnerUuid()));
        sqlBundleList.addAll(
            modifyChildrenCountAndDurationIncludingAncestorsFor(
                entryParentChangedEvent.getParentAfter(),
                childEntry.getChildrenDuration() + childEntry.getDuration(),
                childEntry.getChildrenCount() + 1,
                childEntry.getOwnerUuid()));
        break;
      case "ResourceCreatedEvent":
        return null;
      case "EntryScheduledEvent":
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        toSaveSequenceNumber = entryScheduledEvent.getEntryEventSequenceNumber();
        entryId = entryScheduledEvent.getAggregateId();
        Log.d(TAG, "creating viewModelEntry query for EntryScheduledEvent");
        //Todo: create sqlBudleList => not necessary? viewModelEntry is updated from repository in insert(Entry)
        //Calendar update is done when Event is applied to Resource
        break;
      case "EntryTitleChangedEvent":
        EntryTitleChangedEvent entryTitleChangedEvent = (EntryTitleChangedEvent) event;
        toSaveSequenceNumber = entryTitleChangedEvent.getEventSequenceNumber();
        entryId = entryTitleChangedEvent.getAggregateId();
        break;
      default:
        Log.d(TAG, "Unrecognized: " + event.getClass().getSimpleName());
        toSaveSequenceNumber = -9;
        entryId = "error unrecognized event class";
        break;
    }
    int lastSavedEventSequenceNumber = toSaveSequenceNumber - 1;
    Log.d(TAG, "lastSavedEventSequenceNumber = " + lastSavedEventSequenceNumber);

    if (lastSavedEventSequenceNumber != expectedLastSavedEventSequenceNumber) {
      throw new Exception("Optimistic concurrency exception");
    }

    ContentValues aggregateContentValues = new ContentValues();
    aggregateContentValues.put("aggregateId", entryId);
    aggregateContentValues.put("type", aggregateIdType);
    aggregateContentValues.put("lastSavedEventSequenceNumber", lastSavedEventSequenceNumber + 1);
    sqlBundleList.add(new sqlBundle("aggregates", aggregateContentValues));

    ContentValues eventContentValues = new ContentValues();
    eventContentValues.put("occurredDateTime", event.getOccurredDateTime().toString());
    eventContentValues.put("aggregateId", entryId);
    eventContentValues.put("type", event.getClass().getSimpleName());
    String eventData = jsonOf(event);
    eventContentValues.put("data", eventData);
    eventContentValues.put("eventSequenceNumber", lastSavedEventSequenceNumber + 1);
    sqlBundleList.add(new sqlBundle("events", eventContentValues));

    return sqlBundleList;
  }

  public Entry getEntryWithSavedEventsById(String uuid) {
    EntryCreatedEvent entryCreatedEvent = getEntryCreatedEvent(
        uuid); //Todo: remove as EntryCreatedEvent gets applied so not necessary?
    Entry entry =
        new Entry(
            UUID.fromString(entryCreatedEvent.getOwnerUuid()),
            UUID.fromString(entryCreatedEvent.getParentUuid()),
            UUID.fromString(entryCreatedEvent.getAggregateId()),
            "",
            "",
            0);
    List<Event> eventList = getEventsFor(uuid);
    entry.applyEvents(eventList);
    return entry;
  }

  public Resource getResourceWithSavedEventsById(String uuid) {
    ResourceCreatedEvent resourceCreatedEvent = getResourceCreatedEvent(
        uuid); //Todo: remove as ResourceCreatedEvent gets applied so not necessary?
    //How does it get created?
    Resource resource =
        Resource.Create(
            resourceCreatedEvent.getOwnerEmail(),
            resourceCreatedEvent.getResourceEmail(),
            resourceCreatedEvent.getLifetimeDateTimeRange());
    List<Event> eventList = getEventsFor(uuid);
    resource.applyEvents(eventList);
    return resource;
  }

  public void executeSqlBundles(List<sqlBundle> sqlBundleList) {
    Log.d(TAG, "executeSqlBundles called.");
    // Todo: use conflict rollback to rollback transaction on conflict?

    Log.d(TAG, "sqlBundle length : " + sqlBundleList.size());
    try {
      db.beginTransaction();
      for (sqlBundle sqlBundle : sqlBundleList) {
        Log.d(TAG, "executing sqlBundle " + sqlBundle.toString());
        switch (sqlBundle.getTable()) {
          case "events":
            Log.d(TAG, "case events");
            db.insertOrThrow("events", null, sqlBundle.getContentValues());
            break;
          case "aggregates":
            Log.d(TAG, "case aggregates");
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelCalendar":
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelEntry2":
            db.insertWithOnConflict(
                sqlBundle.getTable(),
                null,
                sqlBundle.getContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE);
            break;
          case "viewModelEntry":
            Log.d(TAG, "case viewModelEntry");
            if (sqlBundle
                .getContentValues()
                .get("lastSavedEventSequenceNumber")
                .toString()
                .equals("0")) {
              db.insertOrThrow("viewModelEntry", null, sqlBundle.getContentValues());
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
      case "EntryDescriptionChangedEvent":
        return gson.toJson(event, EntryDescriptionChangedEvent.class);
      case "EntryDurationChangedEvent":
        return gson.toJson(event, EntryDurationChangedEvent.class);
      case "EntryParentChangedEvent":
        return gson.toJson(event, EntryParentChangedEvent.class);
      case "EntryScheduledEvent":
        return gson.toJson(event, EntryScheduledEvent.class);
      case "ResourceCreatedEvent":
        return gson.toJson(event, ResourceCreatedEvent.class);
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
                  DurationHelper.getDurationStringFromInt(duration));
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
    String SELECT_VIEWMODELENTRY_QUERY = "SELECT json FROM viewModelEntry2 WHERE uuid = ?";

    Cursor cursor = db.rawQuery(SELECT_VIEWMODELENTRY_QUERY, new String[]{entryId});
    String jsonBlob;
    try {
      if (cursor.moveToFirst()) {
        jsonBlob = cursor.getString(cursor.getColumnIndex("json"));
        Gson gson = new GsonBuilder().create();
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

  public List<String> getAncestorIdsExcludingRootForAndIncludingThisParent(
      String parentId, String root) {
    Log.d(TAG, "getAncestorIdsExcludingRootForAndIncludingThisParent for parentId " + parentId +
        " and root " + root);
    String SELECT_ANCESTOR_IDS_QUERY =
        "with recursive\n"
            + "    childEntry(uuid) as (\n"
            + "        values(?)\n"
            + "        union\n"
            + "        select parentUuid\n"
            + "        from viewModelEntry, childEntry\n"
            + "        where viewModelEntry.uuid = childEntry.uuid\n"
            + "    )\n"
            + "select parentUuid from viewModelEntry\n"
            + "    where viewModelEntry.uuid in childEntry;";

    Cursor cursor = db.rawQuery(SELECT_ANCESTOR_IDS_QUERY, new String[]{parentId});
    List<String> ancestorIdList = new ArrayList<>();
    try {
      if (cursor.moveToFirst()) {
        do {
          String parentUuid = cursor.getString(cursor.getColumnIndex("parentUuid"));
          ancestorIdList.add(parentUuid);

        } while (cursor.moveToNext());
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get ancestor ids for uuid");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    if (ancestorIdList.size() > 0) {
      ancestorIdList.remove(root);
    }

    if (!parentId.equals(root)) {
      ancestorIdList.add(parentId);
    }

    Log.d(TAG, "ancestorIdList complete : " + ancestorIdList.toString());
    return ancestorIdList;
  }

  public EntryCreatedEvent getEntryCreatedEvent(String uuid) {

    String SELECT_ENTRYCREATEDEVENT_QUERY =
        "SELECT data FROM events WHERE aggregateId = ? AND eventSequenceNumber = 0";

    Cursor cursor = db.rawQuery(SELECT_ENTRYCREATEDEVENT_QUERY, new String[]{uuid});
    EntryCreatedEvent entryCreatedEvent =
        EntryCreatedEvent.Create(OffsetDateTime.now(ZoneOffset.UTC), "", "", "", -1);
    try {
      if (cursor.moveToFirst()) {
        String dataJson = cursor.getString(cursor.getColumnIndex("data"));
        Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
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

  public ResourceCreatedEvent getResourceCreatedEvent(String uuid) {

    String SELECT_RESOURCECREATEDEVENT_QUERY =
        "SELECT data FROM events WHERE aggregateId = ? AND eventSequenceNumber = 0";

    Cursor cursor = db.rawQuery(SELECT_RESOURCECREATEDEVENT_QUERY, new String[]{uuid});
    ResourceCreatedEvent resourceCreatedEvent =
        ResourceCreatedEvent.Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            new Email("temp"),
            new Email("temp"),
            "",
            "",
            DateTimeRange.Create(
                OffsetDateTime.now(ZoneOffset.UTC),
                OffsetDateTime.now(ZoneOffset.UTC).plusYears(1)),
            -1);
    try {
      if (cursor.moveToFirst()) {
        String dataJson = cursor.getString(cursor.getColumnIndex("data"));
        Gson gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
        resourceCreatedEvent = gson.fromJson(dataJson, ResourceCreatedEvent.class);
      }
    } catch (Exception e) {
      Log.d(TAG, "Error while trying to get resourceCreatedEvent from aggregates");
    } finally {
      if (cursor != null && !cursor.isClosed()) {
        cursor.close();
      }
    }

    return resourceCreatedEvent;
  }

  public List<Event> getEventsFor(String uuid) {

    String SELECT_EVENTS_QUERY = "SELECT type, data FROM events WHERE aggregateId = ?";

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
            case "EntryDescriptionChangedEvent":
              eventList.add(gson.fromJson(dataJson, EntryDescriptionChangedEvent.class));
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
          "DELETE FROM aggregates WHERE type = \"entry\"\n");
      db.execSQL(
          "DELETE FROM viewModelEntry \n");
      db.setTransactionSuccessful();
    } catch (Exception e) {
      Log.d(TAG, "Error while executing deleteAllEntriesFromRepository");
    } finally {
      db.endTransaction();
    }
  }
}
