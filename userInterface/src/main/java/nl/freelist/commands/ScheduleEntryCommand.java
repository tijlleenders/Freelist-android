package nl.freelist.commands;

import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

public class ScheduleEntryCommand extends Command {

  String entryUuid;
  String resourceUuid;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ScheduleEntryCommand(String entryUuid, String resourceUuid,
      int lastSavedEventSequenceNumber, Repository repository) {
    this.entryUuid = entryUuid;
    this.resourceUuid = resourceUuid;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    EntryScheduledEvent entryScheduledEvent = EntryScheduledEvent
        .Create(DateTime.Create("now"), entryUuid, lastSavedEventSequenceNumber + 1);

    Entry entry = repository.getById(entryUuid);
    List<Event> entryEventList = repository.getSavedEventsFor(entryUuid);

    Resource resource = repository.getResourceById(resourceUuid);
    resource.schedule(entry);

    entryEventList.add(entryScheduledEvent);
    entry.applyEvents(entryEventList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);

    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
