package nl.freelist.domain.commands;

import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryTitleCommand extends Command {

  String uuid;
  String titleBefore;
  String titleAfter;
  int lastSavedEventSequenceNumber;
  Repository<Entry> entryRepository;

  public ChangeEntryTitleCommand(String uuid, String titleBefore, String titleAfter,
      int lastSavedEventSequenceNumber, Repository<Entry> entryRepository) {
    this.uuid = uuid;
    this.titleBefore = titleBefore;
    this.titleAfter = titleAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.entryRepository = entryRepository;
  }

  @Override
  public Result execute() {
    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, titleBefore,
            titleAfter);
    Entry entry = entryRepository.getById(uuid);
    List<Event> eventList = entryRepository.getSavedEventsFor(uuid);
    eventList.add(entryTitleChangedEvent);
    entry.applyEvents(eventList);
    entryRepository.insert(entry);
    return new Result(true);
  }
}
