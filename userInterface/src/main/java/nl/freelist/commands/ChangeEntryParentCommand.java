package nl.freelist.commands;

import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryParentCommand extends Command {

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
    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, parentBefore,
            parentAfter);
    Entry entry = repository.getById(uuid);
    List<Event> eventList = repository.getSavedEventsFor(uuid);
    eventList.add(entryParentChangedEvent);
    entry.applyEvents(eventList);

    repository.insert(entry);
    //Todo: update readModel and persist entry in one transaction

    return new Result(true);
  }
}
