package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Calendar;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.Event;

public class ScheduleEntryCommand extends Command {

  String entryUuid;
  String resourceUuid;
  int lastSavedEventSequenceNumber;
  int lastSavedResourceSequenceNumber;
  Repository repository;
  Calendar calendar;

  public ScheduleEntryCommand(String entryUuid, String resourceUuid,
      int lastSavedEventSequenceNumber, int lastSavedResourceSequenceNumber, Repository repository,
      Calendar calendar) {
    this.entryUuid = entryUuid;
    this.resourceUuid = resourceUuid;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
    this.lastSavedResourceSequenceNumber = lastSavedResourceSequenceNumber;
    this.calendar = calendar;
  }

  @Override
  public Result execute() {
    EntryScheduledEvent entryScheduledEvent = EntryScheduledEvent
        .Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            entryUuid,
            resourceUuid,
            lastSavedResourceSequenceNumber + 1,
            lastSavedEventSequenceNumber + 1,
            calendar
        );

    //Todo: create second event for repository (events are about aggregate state changes, so same event can't be shared by two aggregates)

    //How to know if it fails?
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryScheduledEvent);

    Resource resource = repository.getResourceWithSavedEventsById(resourceUuid);
    resource.applyEvents(eventsToAddList);
    List<sqlBundle> sqlBundleListResource = repository.insert(resource);
    if (sqlBundleListResource != null) {
      sqlBundleList.addAll(sqlBundleListResource);
    }

    Entry entry = repository.getEntryWithSavedEventsById(entryUuid);
    entry.applyEvents(eventsToAddList);
    List<sqlBundle> sqlBundleListEntry = repository.insert(entry);
    if (sqlBundleListEntry != null) {
      sqlBundleList.addAll(sqlBundleListEntry);
    }

    if (sqlBundleList != null) {
      repository.executeSqlBundles(sqlBundleList);
      return Result.Create(true, null, "", "");
    }
    return Result.Create(false, null, "", "");
  }
}
