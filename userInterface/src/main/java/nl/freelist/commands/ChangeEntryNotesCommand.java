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
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryNotesCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  String descriptionAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryNotesCommand(
      String uuid,
      String descriptionAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.descriptionAfter = descriptionAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ChangeEntryNotesCommand");

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
    EntryNotesChangedEvent entryNotesChangedEvent = EntryNotesChangedEvent
        .Create(OffsetDateTime.now(ZoneOffset.UTC),
            uuid,
            descriptionAfter
        );
    eventsToAddList.add(entryNotesChangedEvent);
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
