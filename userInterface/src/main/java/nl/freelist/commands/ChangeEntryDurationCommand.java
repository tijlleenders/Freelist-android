package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryDurationCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  long durationAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryDurationCommand(
      String uuid,
      long durationAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.durationAfter = durationAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ChangeEntryDurationCommand");

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
    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent
        .Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            uuid,
            durationAfter
        );
    eventsToAddList.add(entryDurationChangedEvent);
    entry.applyEvents(eventsToAddList);
    try {
      repository.insert(entry);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }
}
