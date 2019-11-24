package nl.freelist.commands;

import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;

public class CreateResourceCommand extends Command {

  UUID resourceUuid;
  UUID ownerUuid;
  Repository repository;

  public CreateResourceCommand(
      String ownerUuid, String resourceUuid, Repository repository) {
    this.ownerUuid = UUID.fromString(ownerUuid);
    this.resourceUuid = UUID.fromString(resourceUuid);
    this.repository = repository;
  }

  @Override
  public Result execute() {
//    Todo: make it possible to add resources from UI - for now only one resource hardcoded via MySettings
//    Todo: Resource.Create(ownerUuid) method with checks if doesn't exist yet
//    ResourceCreatedEvent resourceCreatedEvent =
//        ResourceCreatedEvent.Create(
//            DateTime.Create("now"),
//            ownerUuid.toString(),
//            resourceUuid.toString()
//            );
//    resource.applyEvent(resourceCreatedEvent);
//    List<sqlBundle> sqlBundleList = repository.insert(resource);
//    repository.executeSqlBundles(sqlBundleList);
    return Result.Create(true, null, "", "");
  }
}
