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
import nl.freelist.domain.events.EntryCreatedEvent;
import nl.freelist.domain.events.EntryDurationChangedEvent;
import nl.freelist.domain.events.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.EntryNotesChangedEvent;
import nl.freelist.domain.events.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.EntryTitleChangedEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class SaveEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String aggregateUuid;
  String parentUuid;
  String ownerUuid;
  String titleAfter;
  OffsetDateTime startDateTimeAfter;
  long durationAfter;
  OffsetDateTime endDateTimeAfter;
  String notesAfter;
  List<DtrConstraint> preferredDaysConstraintsAfter;
  int lastSavedEventSequenceNumber;
  Repository repository;

  public SaveEntryCommand(
      String aggregateUuid,
      String parentUuid,
      String ownerUuid,
      String titleAfter,
      OffsetDateTime startDateTimeAfterAfter,
      long durationAfter,
      OffsetDateTime endDateTimeAfter,
      String notesAfter,
      List<DtrConstraint> preferredDaysConstraintsAfter,
      int lastSavedEventSequenceNumber,
      Repository repository
  ) {
    this.aggregateUuid = aggregateUuid;
    this.parentUuid = parentUuid;
    this.ownerUuid = ownerUuid;
    this.titleAfter = titleAfter;
    this.startDateTimeAfter = startDateTimeAfterAfter;
    this.durationAfter = durationAfter;
    this.endDateTimeAfter = endDateTimeAfter;
    this.notesAfter = notesAfter;
    this.preferredDaysConstraintsAfter = preferredDaysConstraintsAfter;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing SaveEntryCommand");

    Entry entry = repository.getEntryWithSavedEventsById(aggregateUuid);
    List<Event> eventsToAddList = new ArrayList<>();

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

    if (lastSavedEventSequenceNumber == -1) {
      EntryCreatedEvent entryCreatedEvent = EntryCreatedEvent.Create(
          now,
          ownerUuid,
          parentUuid,
          aggregateUuid
      );
      eventsToAddList.add(entryCreatedEvent);
    }


    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(
            now,
            aggregateUuid,
            titleAfter);

    EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent = EntryStartDateTimeChangedEvent
        .Create(
            now,
            aggregateUuid,
            startDateTimeAfter
        );

    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent.Create(
        OffsetDateTime.now(ZoneOffset.UTC),
        aggregateUuid,
        durationAfter
    );

    EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent = EntryEndDateTimeChangedEvent.Create(
        now,
        aggregateUuid,
        endDateTimeAfter
    );

    EntryNotesChangedEvent entryNotesChangedEvent = EntryNotesChangedEvent.Create(
        now,
        aggregateUuid,
        notesAfter
    );

    EntryPreferredDayConstraintsChangedEvent entryPreferredDayConstraintsChangedEvent = EntryPreferredDayConstraintsChangedEvent
        .Create(
            now,
            aggregateUuid,
            preferredDaysConstraintsAfter
        );

    //Todo: add parentchange
//    EntryParentChangedEvent entryParentChangedEvent = EntryParentChangedEvent.Create(
//      now,
//      uuid,
//        parentAfter
//    );

    eventsToAddList.add(entryTitleChangedEvent);
    eventsToAddList.add(entryStartDateTimeChangedEvent);
    eventsToAddList.add(entryDurationChangedEvent);
    eventsToAddList.add(entryEndDateTimeChangedEvent);
    eventsToAddList.add(entryNotesChangedEvent);
    eventsToAddList.add(entryPreferredDayConstraintsChangedEvent);
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
