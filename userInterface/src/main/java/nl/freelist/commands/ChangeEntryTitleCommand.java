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
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryTitleCommand extends Command {

  String uuid;
  String titleBefore;
  String titleAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryTitleCommand(String uuid, String titleBefore, String titleAfter,
      int lastSavedEventSequenceNumber, Repository repository) {
    this.uuid = uuid;
    this.titleBefore = titleBefore;
    this.titleAfter = titleAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(OffsetDateTime.now(ZoneOffset.UTC), uuid, lastSavedEventSequenceNumber + 1,
            titleBefore,
            titleAfter);
    Entry entry = repository.getEntryWithSavedEventsById(uuid);
    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryTitleChangedEvent);
    entry.applyEvents(eventsToAddList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
