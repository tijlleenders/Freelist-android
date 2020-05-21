package nl.freelist.commands;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.data.Repository;
import nl.freelist.domain.aggregates.scheduler.Scheduler;
import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class SaveEntryCommand extends Command {

  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  Id entryId;
  Id parentId;
  Id personId;
  String titleAfter;
  OffsetDateTime startDateTimeAfter;
  long durationAfter;
  OffsetDateTime endDateTimeAfter;
  String notesAfter; // Todo: move to own Aggegate just like comments - has it's own consistency
  // boundary
  List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter;
  int UiLastSavedSchedulerEventSequenceNumber;
  Repository repository;
  Scheduler scheduler;

  public SaveEntryCommand(
      String entryId,
      String parentId,
      String personId,
      String titleAfter,
      OffsetDateTime startDateTimeAfterAfter,
      long durationAfter,
      OffsetDateTime endDateTimeAfter,
      String notesAfter,
      List<ImpossibleDaysConstraint> impossibleDaysConstraintsAfter,
      int UiLastSavedSchedulerEventSequenceNumber,
      Repository repository) {
    this.entryId = Id.fromString(entryId);
    this.parentId = Id.fromString(parentId);
    this.personId = Id.fromString(personId);
    this.titleAfter = titleAfter;
    this.startDateTimeAfter = startDateTimeAfterAfter;
    this.durationAfter = durationAfter;
    this.endDateTimeAfter = endDateTimeAfter;
    this.notesAfter = notesAfter;
    this.impossibleDaysConstraintsAfter = impossibleDaysConstraintsAfter;
    this.UiLastSavedSchedulerEventSequenceNumber = UiLastSavedSchedulerEventSequenceNumber;
    this.repository = repository;
  }

  @Override
  public Result execute() {
    LOGGER.log(Level.INFO, "Executing SaveEntryCommand");

    Scheduler scheduler;
    scheduler = repository.getSchedulerWithEventsById(personId);

    if (UiLastSavedSchedulerEventSequenceNumber == -1) {
      UiLastSavedSchedulerEventSequenceNumber += 1;
    }

    try {
      scheduler.upsert(
          entryId,
          parentId,
          titleAfter,
          startDateTimeAfter,
          durationAfter,
          endDateTimeAfter,
          notesAfter,
          impossibleDaysConstraintsAfter,
          UiLastSavedSchedulerEventSequenceNumber);
    } catch (Exception e) {
      if (e.getMessage() != null) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      return Result.Create(false, null, "", e.getMessage());
    }

    try {
      repository.insert(scheduler);
    } catch (Exception e) {
      if (e.getMessage() != null) {
        LOGGER.log(Level.WARNING, e.getMessage());
      }
      return Result.Create(false, null, "", e.getMessage());
    }

    return Result.Create(true, null, "", "Scheduler inserted");
  }
}
