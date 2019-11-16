package nl.freelist.commands;

import java.util.UUID;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;

public class DeleteAllEntriesInRepositoryCommand extends Command {

  UUID uuid;
  UUID parentUuid;
  UUID ownerUuid;
  Repository repository;

  public DeleteAllEntriesInRepositoryCommand(Repository repository) {
    this.repository = repository;
  }

  @Override
  public Result execute() {
//    EntryCreatedEvent entryCreatedEvent =
//        EntryCreatedEvent.Create(
//            DateTime.Create("now"),
//            ownerUuid.toString(),
//            parentUuid.toString(),
//            uuid.toString(),
//            0);
//    List<sqlBundle> sqlBundleList = repository.insert(entry);
//    repository.executeSqlBundles(sqlBundleList);
    return new Result(true);
  }
}
