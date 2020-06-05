package nl.freelist.domain.aggregates.scheduler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.crossCuttingConcerns.Constants;
import nl.freelist.domain.crossCuttingConcerns.DurationStartDateTimeKeyComparator;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.scheduler.calendar.EntryNotScheduledEvent;
import nl.freelist.domain.events.scheduler.calendar.EntryScheduledEvent;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.TimeSlot;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class Calendar {

  private static final Logger LOGGER = Logger.getLogger(Calendar.class.getName());

  private TreeMap<Long, TimeSlot> freeTimeSlots = new TreeMap<>(
      new DurationStartDateTimeKeyComparator());
  private HashMap<Id, TimeSlot> scheduledTimeSlots = new HashMap<>();
  private List<Event> eventList =
      new ArrayList<>(); // needed as all events are stored on Scheduler? leave for now as helps in
  // debugging

  private Calendar(
  ) {
    LOGGER.log(Level.INFO, "Entry initiated without events.");
  }

  public static Calendar Create() {
    Calendar calendar = new Calendar();
    TimeSlot freeForeverTimeSlot = TimeSlot.CreateFreeForever();
    calendar.freeTimeSlots.put(freeForeverTimeSlot.getKey(), freeForeverTimeSlot);
    return calendar;
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?

    if (event == null) {
      return;
    }
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "EntryScheduledEvent":
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        freeTimeSlots.remove(entryScheduledEvent.getFreeTimeSlotToDelete().getKey());
        for (TimeSlot freeTimeSlotToCreate : entryScheduledEvent.getFreeTimeSlotsToCreate()) {
          freeTimeSlots.put(freeTimeSlotToCreate.getKey(), freeTimeSlotToCreate);
        }
        scheduledTimeSlots.put(
            entryScheduledEvent.getScheduledTimeSlot().getEntryId(),
            entryScheduledEvent.getScheduledTimeSlot());
        break;
      case "EntryNotScheduledEvent":
        EntryNotScheduledEvent entryNotScheduledEvent = (EntryNotScheduledEvent) event;
        scheduledTimeSlots.remove(entryNotScheduledEvent.getEntryId());

        //Todo: add scheduled time back to free time
        break;
      default:
        LOGGER.log(
            Level.SEVERE,
            "Event can't be applied to calendar - event type " + eventClass + " not recognized");
        break;
    }
    eventList.add(event);
  }

  public void printFreeTimeSlots() {
    System.out.println();
    System.out.println();
    System.out.printf("Free TimeSlots:\n");
    for (TimeSlot timeSlot : freeTimeSlots.values()) {
      printTimeSlot(timeSlot);
    }
  }

  public void printScheduledTimeSlots() {
    System.out.println();
    System.out.println();
    System.out.printf("Scheduled TimeSlots:\n");
    for (TimeSlot timeSlot : scheduledTimeSlots.values()) {
      printTimeSlot(timeSlot);
    }
  }

  private void printTimeSlot(TimeSlot timeSlot) {
    System.out.printf("Duration: %s\n", timeSlot.getDuration());
    System.out.printf("Key: %s\n", timeSlot.getKey());
    System.out.printf(
        "DateTimes:\n%s\n%s\n", timeSlot.getStartDateTime(), timeSlot.getEndDateTime());
    System.out.printf("EntryId: %s\n", timeSlot.getEntryId());
    System.out.println();
  }

  public void applyEvents(List<Event> eventList) {
    for (Event event : eventList) {
      applyEvent(event);
    }
  }

  public TimeSlot getScheduledTimeSlot(Id entryId) {
    return scheduledTimeSlots.get(entryId);
  }

  public TimeSlot getCompatibleFreeTimeSlots(Entry entry) {
    long duration = entry.getDuration();
    OffsetDateTime startAtOrAfter = entry.getStartAtOrAfterDateTime();
    OffsetDateTime finishAtOrBefore = entry.getFinishAtOrBeforeDateTime();
    if (duration == 0) {
      return null;
    }
    long searchKey = (duration << 32) | (startAtOrAfter.toEpochSecond() - Constants.START_OF_TIME
        .toEpochSecond());
    NavigableMap<Long, TimeSlot> tempFreeTimeSlots = freeTimeSlots.headMap(searchKey, true);

    outerloop:
    for (TimeSlot timeSlot : tempFreeTimeSlots.values()) {
      if (timeSlot.getStartDateTime().toEpochSecond()
          > (finishAtOrBefore.toEpochSecond() - duration)
          && timeSlot.getEndDateTime().toEpochSecond() < finishAtOrBefore.toEpochSecond()) {
        continue outerloop; //to next candidate
      }
      for (ImpossibleDaysConstraint impossibleDaysConstraint : entry
          .getImpossibleDaysConstraints()) {
        if (!impossibleDaysConstraint.validate(timeSlot)) {
          continue outerloop;
        }
      }
      return timeSlot;
    }
    return null;
  }

  public TimeSlot getFreeTimeSlot(Long key) {
    return freeTimeSlots.get(key);
  }
}
