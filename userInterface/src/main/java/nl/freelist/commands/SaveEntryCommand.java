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
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;

public class SaveEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String uuid;
  String titleAfter;
  OffsetDateTime startDateTimeAfter;
  long durationAfter;
  OffsetDateTime endDateTimeAfter;
  String notesAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public SaveEntryCommand(
      String uuid,
      String titleAfter,
      OffsetDateTime startDateTimeAfterAfter,
      long durationAfter,
      OffsetDateTime endDateTimeAfter,
      String notesAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.uuid = uuid;
    this.titleAfter = titleAfter;
    this.startDateTimeAfter = startDateTimeAfterAfter;
    this.durationAfter = durationAfter;
    this.endDateTimeAfter = endDateTimeAfter;
    this.notesAfter = notesAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing SaveEntryCommand");

    Entry entry = repository.getEntryWithSavedEventsById(uuid);

    if (entry.getLastAppliedEventSequenceNumber() != lastSavedEventSequenceNumber) {
      LOGGER.log(Level.WARNING, "Optimistic concurrency exception: "
          + "Entry:"
          + entry.getLastAppliedEventSequenceNumber()
          + " UI:"
          + lastSavedEventSequenceNumber);
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

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(
            now,
            uuid,
            titleAfter);

    EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent = EntryStartDateTimeChangedEvent
        .Create(
            now,
            uuid,
            startDateTimeAfter
        );

    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent.Create(
        OffsetDateTime.now(ZoneOffset.UTC),
        uuid,
        durationAfter
    );

    EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent = EntryEndDateTimeChangedEvent.Create(
        now,
        uuid,
        endDateTimeAfter
    );

    EntryNotesChangedEvent entryNotesChangedEvent = EntryNotesChangedEvent.Create(
        now,
        uuid,
        notesAfter
    );

    //Todo: add parentchange
//    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent.Create(
//      now,
//      uuid,
//        parentAfter
//    );

    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryTitleChangedEvent);
    eventsToAddList.add(entryStartDateTimeChangedEvent);
    eventsToAddList.add(entryDurationChangedEvent);
    eventsToAddList.add(entryEndDateTimeChangedEvent);
    eventsToAddList.add(entryNotesChangedEvent);
    //Todo: parent
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
