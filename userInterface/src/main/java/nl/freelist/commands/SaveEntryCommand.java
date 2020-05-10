package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.domain.aggregates.entry.Entry;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.entry.EntryCreatedEvent;
import nl.freelist.domain.events.entry.EntryDurationChangedEvent;
import nl.freelist.domain.events.entry.EntryEndDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryNotesChangedEvent;
import nl.freelist.domain.events.entry.EntryPreferredDayConstraintsChangedEvent;
import nl.freelist.domain.events.entry.EntryStartDateTimeChangedEvent;
import nl.freelist.domain.events.entry.EntryTitleChangedEvent;
import nl.freelist.domain.valueObjects.DtrConstraint;
import nl.freelist.domain.valueObjects.Id;

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

    Entry entry = repository.getEntryWithSavedEventsById(Id.fromString(aggregateUuid));
    List<Event> eventsToAddList = new ArrayList<>();

    // Optimistic locking only necessary in multi-user environment or if commands out of order
    // now only UI + all commands are scheduled sequentially on same thread

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    if (lastSavedEventSequenceNumber == -1) {
      EntryCreatedEvent entryCreatedEvent = EntryCreatedEvent.Create(
          now,
          Id.fromString(ownerUuid),
          Id.fromString(parentUuid),
          Id.fromString(aggregateUuid)
      );
      eventsToAddList.add(entryCreatedEvent);
    }


    EntryTitleChangedEvent entryTitleChangedEvent = EntryTitleChangedEvent
        .Create(
            now,
            Id.fromString(aggregateUuid),
            titleAfter);

    EntryStartDateTimeChangedEvent entryStartDateTimeChangedEvent = EntryStartDateTimeChangedEvent
        .Create(
            now,
            Id.fromString(aggregateUuid),
            startDateTimeAfter
        );

    EntryDurationChangedEvent entryDurationChangedEvent = EntryDurationChangedEvent.Create(
        OffsetDateTime.now(ZoneOffset.UTC),
        Id.fromString(aggregateUuid),
        durationAfter
    );

    EntryEndDateTimeChangedEvent entryEndDateTimeChangedEvent = EntryEndDateTimeChangedEvent.Create(
        now,
        Id.fromString(aggregateUuid),
        endDateTimeAfter
    );

    EntryNotesChangedEvent entryNotesChangedEvent = EntryNotesChangedEvent.Create(
        now,
        Id.fromString(aggregateUuid),
        notesAfter
    );

    EntryPreferredDayConstraintsChangedEvent entryPreferredDayConstraintsChangedEvent = EntryPreferredDayConstraintsChangedEvent
        .Create(
            now,
            Id.fromString(aggregateUuid),
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
      if (e.getMessage() != null) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      return Result.Create(false, null, "", e.getMessage());
    }

    return Result.Create(true, null, "", "");
  }
}
