package nl.freelist.commands;

import java.util.List;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDescriptionChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DateTime;

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
        .Create(DateTime.Create("now"), uuid, lastSavedEventSequenceNumber + 1, descriptionBefore,
            descriptionAfter);
    Entry entry = repository.getById(uuid);
    List<Event> eventList = repository.getSavedEventsFor(uuid);
    eventList.add(entryDescriptionChangedEvent);
    entry.applyEvents(eventList);
    repository.insert(entry);
    return new Result(true);
  }
}
