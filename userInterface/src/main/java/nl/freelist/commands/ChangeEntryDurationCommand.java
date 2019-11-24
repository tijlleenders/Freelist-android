package nl.freelist.commands;

import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryDurationCommand extends Command {

  String uuid;
  int durationBefore;
  int durationAfter;
  String unitOfMeasure;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryDurationCommand(String uuid, int durationBefore, int durationAfter,
      String unitOfMeasure,
      int lastSavedEventSequenceNumber, Repository repository) {
    this.uuid = uuid;
    this.durationBefore = durationBefore;
    this.durationAfter = durationAfter;
    this.unitOfMeasure = unitOfMeasure;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, durationBefore,
            durationAfter, unitOfMeasure);
    Entry entry = repository.getById(uuid);
    List<Event> eventList = repository.getSavedEventsFor(uuid);
    eventList.add(entryDurationChangedEvent);
    entry.applyEvents(eventList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
