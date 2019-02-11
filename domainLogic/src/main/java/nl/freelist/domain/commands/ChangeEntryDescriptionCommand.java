package nl.freelist.domain.commands;

import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.valueObjects.DateTime;

public class ChangeEntryDescriptionCommand extends Command {

  String uuid;
  String descriptionBefore;
  String descriptionAfter;
  int lastSavedEventSequenceNumber;
  Repository<Entry> entryRepository;

  public ChangeEntryDescriptionCommand(String uuid, String descriptionBefore,
      String descriptionAfter,
      int lastSavedEventSequenceNumber, Repository<Entry> entryRepository) {
    this.uuid = uuid;
    this.descriptionBefore = descriptionBefore;
    this.descriptionAfter = descriptionAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.entryRepository = entryRepository;
  }

  @Override
  public Result execute() {
    EntryDescriptionChangedEvent entryDescriptionChangedEvent = EntryDescriptionChangedEvent
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, descriptionBefore,
            descriptionAfter);
    Entry entry = entryRepository.getById(uuid);
    List<Event> eventList = entryRepository.getSavedEventsFor(uuid);
    eventList.add(entryDescriptionChangedEvent);
    entry.applyEvents(eventList);
    entryRepository.insert(entry);
    return new Result(true);
  }
}
