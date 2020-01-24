package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryCreatedEvent;

public class CreateEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

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
    LOGGER.log(Level.INFO, "Executing CreateEntryCommand");

    Entry entry = new Entry();
    EntryCreatedEvent entryCreatedEvent =
        EntryCreatedEvent.Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            ownerUuid.toString(),
            parentUuid.toString(),
            uuid.toString()
        );
    entry.applyEvent(entryCreatedEvent);
    try {
      List<sqlBundle> sqlBundleList = repository.insert(entry);
      repository.executeSqlBundles(sqlBundleList);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }
}
