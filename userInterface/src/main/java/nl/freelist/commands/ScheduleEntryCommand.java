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
import nl.freelist.domain.entities.Calendar;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.entities.Resource;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.Event;

public class ScheduleEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  String entryUuid;
  String resourceUuid;
  int lastSavedEventSequenceNumber;
  int lastSavedResourceSequenceNumber;
  Repository repository;
  Calendar calendar;

  public ScheduleEntryCommand(String entryUuid, String resourceUuid,
      int lastSavedEventSequenceNumber, int lastSavedResourceSequenceNumber, Repository repository,
      Calendar calendar) {
    this.entryUuid = entryUuid;
    this.resourceUuid = resourceUuid;
    this.lastSavedEventSequenceNumber = lastSavedEventSequenceNumber;
    this.repository = repository;
    this.lastSavedResourceSequenceNumber = lastSavedResourceSequenceNumber;
    this.calendar = calendar;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing ScheduleEntryCommand");

    Entry entry = repository.getEntryWithSavedEventsById(entryUuid);
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

    Resource resource = repository.getResourceWithSavedEventsById(resourceUuid);
    //Todo: Check resource optimistic concurrency

    EntryScheduledEvent entryScheduledEvent = EntryScheduledEvent
        .Create(
            OffsetDateTime.now(ZoneOffset.UTC),
            entryUuid,
            resourceUuid,
            calendar
        );

    //Todo: create second event for entry (events are about aggregate state changes, so same event can't be shared by two aggregates)
    // the resource state change triggers the entry state change (event added to list) in the same transaction

    //How to know if it fails?
    List<sqlBundle> sqlBundleList = new ArrayList<>();
    List<Event> eventsToAddList = new ArrayList<>();
    eventsToAddList.add(entryScheduledEvent);

    resource.applyEvents(eventsToAddList);

    try {
      repository.insert(resource);
    } catch (Exception e) {
      LOGGER.log(Level.WARNING, e.getMessage());
      return Result.Create(false, null, "", e.getMessage());
    }
      return Result.Create(true, null, "", "");
  }
}
