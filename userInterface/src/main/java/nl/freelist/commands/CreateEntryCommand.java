package nl.freelist.commands;

import java.util.List;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
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
    List<sqlBundle> sqlBundleList = repository.insert(entry);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
