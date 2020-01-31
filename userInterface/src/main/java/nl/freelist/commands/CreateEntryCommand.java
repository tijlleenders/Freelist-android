package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.androidCrossCuttingConcerns.MySettings;
import nl.freelist.data.Repository;
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
  MySettings mySettings;

  public CreateEntryCommand(
      //Todo: is validation logic in constructor OK to do? Or move to static Create method
      String ownerUuid, String parentUuid, String uuid, Repository repository) {
    if (ownerUuid != null) {
      this.ownerUuid = UUID.fromString(ownerUuid);
    } else {
      this.ownerUuid = UUID.fromString(mySettings.getResourceUuid());
    }
    if (parentUuid != null) {
      this.parentUuid = UUID.fromString(parentUuid);
    } else {
      this.parentUuid = UUID.fromString(mySettings.getResourceUuid());
    }
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
      repository.insert(entry);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }
}
