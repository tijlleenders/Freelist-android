package nl.freelist.commands;

import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.valueObjects.DateTime;

public class CreateEntryCommand extends Command {

  UUID uuid;
  UUID parentUuid;
  UUID ownerUuid;
  Repository repository;

  public CreateEntryCommand(
      String ownerUuid, String parentUuid, String uuid, Repository repository) {
    this.ownerUuid = UUID.fromString(ownerUuid);
    this.parentUuid = UUID.fromString(parentUuid);
    this.uuid = UUID.fromString(uuid);
    this.repository = repository;
  }

  @Override
  public Result execute() {
    Entry entry = new Entry(ownerUuid, parentUuid, uuid, "", "", 0);
    EntryCreatedEvent entryCreatedEvent =
        EntryCreatedEvent.Create(
            DateTime.Create("now"),
            ownerUuid.toString(),
            parentUuid.toString(),
            uuid.toString(),
            0);
    entry.applyEvent(entryCreatedEvent);
    // When entry is persisted (by an external actor, ie this execute method of the command)
    // the events with sequence number above the one seen in the database
    // are fetched and persisted
    // The entry itself does not save
    // and does not know what is saved or not
    // it just keeps track of the lastAppliedEventSequenceNumber for itself
    repository.insert(entry);
    return new Result(true);
  }
}
