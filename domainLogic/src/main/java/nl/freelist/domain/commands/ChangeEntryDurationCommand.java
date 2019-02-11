package nl.freelist.domain.commands;

import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryDurationCommand extends Command {

  String uuid;
  int durationBefore;
  int durationAfter;
  String unitOfMeasure;
  int lastSavedEventSequenceNumber;
  Repository<Entry> entryRepository;

  public ChangeEntryDurationCommand(String uuid, int durationBefore, int durationAfter,
      String unitOfMeasure,
      int lastSavedEventSequenceNumber, Repository<Entry> entryRepository) {
    this.uuid = uuid;
    this.durationBefore = durationBefore;
    this.durationAfter = durationAfter;
    this.unitOfMeasure = unitOfMeasure;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.entryRepository = entryRepository;
  }

  @Override
  public Result execute() {
    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, durationBefore,
            durationAfter, unitOfMeasure);
    Entry entry = entryRepository.getById(uuid);
    List<Event> eventList = entryRepository.getSavedEventsFor(uuid);
    eventList.add(entryDurationChangedEvent);
    entry.applyEvents(eventList);
    entryRepository.insert(entry);
    return new Result(true);
  }
}
