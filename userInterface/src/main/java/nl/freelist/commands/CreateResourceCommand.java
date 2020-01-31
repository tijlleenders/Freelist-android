package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.ResourceCreatedEvent;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

public class CreateResourceCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  Email ownerEmail;
  Email resourceEmail;
  UUID resourceUuid;
  UUID ownerUuid;
  Repository repository;
  DateTimeRange lifetimeDateTimeRange;

  public CreateResourceCommand(
      Email ownerEmail,
      Email resourceEmail,
      DateTimeRange lifetimeDateTimeRange,
      Repository repository
  ) {
    this.ownerEmail = ownerEmail;
    this.resourceEmail = resourceEmail;
    this.ownerUuid = UUID.nameUUIDFromBytes(ownerEmail.getEmailString().getBytes());
    this.resourceUuid = UUID.nameUUIDFromBytes(resourceEmail.getEmailString().getBytes());
    this.lifetimeDateTimeRange = lifetimeDateTimeRange;
    this.repository = repository;

  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing CreateResourceCommand");
//    Todo: make it possible to add resources from UI - for now only one default resource anonymous@freelist.nl on initial startup
    Resource resource = Resource.Create();
    ResourceCreatedEvent resourceCreatedEvent =
        ResourceCreatedEvent.Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            ownerEmail,
            resourceEmail,
            ownerUuid.toString(),
            resourceUuid.toString(),
            lifetimeDateTimeRange,
            0 //Todo: remove and add check in Resource.applyEvent
        );
    resource.applyEvent(resourceCreatedEvent);
    repository.insert(resource);
    //Todo: log if not successful + return false Result object
    return Result.Create(true, null, "", "");
  }
}
