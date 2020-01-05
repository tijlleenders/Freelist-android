package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.ResourceCreatedEvent;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

public class CreateResourceCommand extends Command {

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
            0
        );
    resource.applyEvent(resourceCreatedEvent);
    List<sqlBundle> sqlBundleList = repository.insert(resource);
    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
