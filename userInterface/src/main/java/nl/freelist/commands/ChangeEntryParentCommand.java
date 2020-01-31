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
import nl.freelist.domain.events.EntryParentChangedEvent;
import nl.freelist.domain.events.Event;

public class ChangeEntryParentCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  String parentAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public ChangeEntryParentCommand(
      String uuid,
      String parentAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.parentAfter = parentAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(
        Level.INFO,
        "ChangeEntryParentCommand executed for "
            + uuid
            + " and parentAfter "
            + parentAfter
    );

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
    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent.Create(
        OffsetDateTime.now(ZoneOffset.UTC),
        uuid,
        parentAfter
    );
    eventsToAddList.add(entryParentChangedEvent);
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
