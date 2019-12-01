package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryDescriptionCommand extends Command {

  String uuid;
  String descriptionBefore;
  String descriptionAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryDescriptionCommand(String uuid, String descriptionBefore,
      String descriptionAfter,
      int lastSavedEventSequenceNumber, Repository repository) {
    this.uuid = uuid;
    this.descriptionBefore = descriptionBefore;
    this.descriptionAfter = descriptionAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    EntryDescriptionChangedEvent entryDescriptionChangedEvent = EntryDescriptionChangedEvent
        .Create(OffsetDateTime.now(ZoneOffset.UTC), uuid, lastSavedEventSequenceNumber + 1,
            descriptionBefore,
            descriptionAfter);
    Entry entry = repository.getEntryWithSavedEventsById(uuid);
    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryDescriptionChangedEvent);
    entry.applyEvents(eventsToAddList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
