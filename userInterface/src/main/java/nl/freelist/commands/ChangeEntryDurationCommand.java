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
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.Event;

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
        .Create(OffsetDateTime.now(ZoneOffset.UTC), uuid, lastSavedEventSequenceNumber + 1,
            durationBefore,
            durationAfter, unitOfMeasure);
    Entry entry = repository.getEntryWithSavedEventsById(uuid);
    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryDurationChangedEvent);
    entry.applyEvents(eventsToAddList);
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
