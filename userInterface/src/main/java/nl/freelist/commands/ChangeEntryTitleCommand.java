package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.data.sqlBundle;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryTitleCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  String titleAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryTitleCommand(
      String uuid,
      String titleAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.titleAfter = titleAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ChangeEntryTitleCommand");

    Entry entry = repository.getEntryWithSavedEventsById(uuid);
    if (entry.getLastAppliedEventSequenceNumber() != lastSavedEventSequenceNumber) {
      return Result.Create(
          false,
          null,
          "",
          "Optimistic concurrency exception: "
              + "Entry:"
              + entry.getLastAppliedEventSequenceNumber()
              + " UI:"
              + lastSavedEventSequenceNumber
      );
    }

    List<Event> eventsToAddList = new ArrayList<>();
    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            uuid,
            titleAfter);
    eventsToAddList.add(entryTitleChangedEvent);
    entry.applyEvents(eventsToAddList);
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
