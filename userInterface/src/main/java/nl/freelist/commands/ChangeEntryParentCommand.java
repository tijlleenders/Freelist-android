package nl.freelist.commands;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryParentCommand extends Command {

  private static final Logger LOGGER = Logger.getLogger(ChangeEntryParentCommand.class.getName());

  String uuid;
  String parentBefore;
  String parentAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryParentCommand(String uuid, String parentBefore, String parentAfter,
      int lastSavedEventSequenceNumber, Repository repository) {
    this.uuid = uuid;
    this.parentBefore = parentBefore;
    this.parentAfter = parentAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO,
        "ChangeEntryParentCommand executed for " + uuid + " with parentBefore " + parentBefore +
            " and "
            + "parentAfter "
            + parentAfter);
    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, parentBefore,
            parentAfter);
    Entry entry = repository.getById(uuid);
    List<Event> eventList = repository.getSavedEventsFor(uuid);
    eventList.add(entryParentChangedEvent);
    entry.applyEvents(eventList);

    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);

    return new Result(true);
  }
}
