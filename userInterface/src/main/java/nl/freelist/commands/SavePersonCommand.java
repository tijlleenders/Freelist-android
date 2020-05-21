package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.domain.aggregates.Person;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.person.PersonCreatedEvent;
import nl.freelist.domain.valueObjects.Id;

public class SavePersonCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String personId;
  Repository repository;
  int lastSavedEventSequenceNumber;

  public SavePersonCommand(
      String personId,
      Repository repository
  ) {
    this.personId = personId;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing SavePersonCommand");
//    Todo: make it possible to add resources from UI - for now only one default person anonymous@freelist.nl on initial startup

    Person person = repository.getPersonWithSavedEventsById(Id.fromString(personId));

    List<Event> eventsToAddList = new ArrayList<>();

    // Optimistic locking only necessary in multi-user environment or if commands out of order
    // now only UI + all commands are scheduled sequentially on same thread

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    if (lastSavedEventSequenceNumber == -1) {
      PersonCreatedEvent personCreatedEvent =
          PersonCreatedEvent.Create(now, Id.fromString(personId));
      eventsToAddList.add(personCreatedEvent);
    }

//    repository.insert(person);
    //Todo: log if not successful + return false Result object
    return Result.Create(true, null, "", "");
  }
}
