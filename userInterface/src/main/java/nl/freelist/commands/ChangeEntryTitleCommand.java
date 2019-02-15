package nl.freelist.commands;

import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

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
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, titleBefore,
            titleAfter);
    Entry entry = repository.getById(uuid);
    List<Event> eventList = repository.getSavedEventsFor(uuid);
    eventList.add(entryTitleChangedEvent);
    entry.applyEvents(eventList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return new Result(true);
  }
}
