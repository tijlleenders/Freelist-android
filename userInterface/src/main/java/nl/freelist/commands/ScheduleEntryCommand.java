package nl.freelist.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Person;
import nl.freelist.domain.events.Event;

public class ScheduleEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String entryUuid;
  String resourceUuid;
  Repository repository;

  public ScheduleEntryCommand(String entryUuid, Repository repository) {
    this.entryUuid = entryUuid;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ScheduleEntryCommand");

    Entry entry = repository.getEntryWithSavedEventsById(entryUuid);

    Person person = repository.getResourceWithSavedEventsById(entry.getOwnerUuid().toString());

//    EntryScheduledEvent entryScheduledEvent = EntryScheduledEvent
//        .Create(
//            OffsetDateTime.now(ZoneOffset.UTC),
//            entryUuid,
//            resourceUuid,
//            appointment
//        );

    //Todo: create second event for entry (events are about aggregate state changes, so same event can't be shared by two aggregates)
    // the person state change triggers the entry state change (event added to list) in the same transaction

    //How to know if it fails?
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    List<Event> eventsToAddList = new ArrayList<>();
//    eventsToAddList.add(entryScheduledEvent);

    person.applyEvents(eventsToAddList);

    try {
      repository.insert(person);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
      return Result.Create(true, null, "", "");
  }
}
