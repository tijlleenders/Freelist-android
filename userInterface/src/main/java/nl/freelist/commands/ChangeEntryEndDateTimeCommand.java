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
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryEndDateTimeCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  OffsetDateTime endDateTimeAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryEndDateTimeCommand(
      String uuid,
      OffsetDateTime endDateTimeAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.endDateTimeAfter = endDateTimeAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ChangeEntryEndDateTimeCommand");

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
    EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent = EntryEndDateTimeChangedEvent.Create(
        OffsetDateTime.now(ZoneOffset.UTC),
        uuid,
        endDateTimeAfter
    );
    eventsToAddList.add(entryEndDateTimeChangedEvent);
    entry.applyEvents(eventsToAddList);
    try {
      repository.insert(entry);
    } catch (Exception e) {
      LOGGER.log(Level.FINE, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
    return Result.Create(true, null, "", "");
  }
}
