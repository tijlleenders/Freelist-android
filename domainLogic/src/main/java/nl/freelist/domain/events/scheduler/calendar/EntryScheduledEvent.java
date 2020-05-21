package nl.freelist.domain.events.scheduler.calendar;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.TimeSlot;

public class EntryScheduledEvent extends Event {

  private TimeSlot scheduledTimeSlot;
  private TimeSlot freeTimeSlotToDelete;
  private List<TimeSlot> freeTimeSlotsToCreate = new ArrayList<>();
  private String eventType = "EntryScheduledEvent";

  private EntryScheduledEvent(
      OffsetDateTime occurredDateTime, Id personId, TimeSlot scheduledTimeSlot,
      TimeSlot freeTimeSlotToDelete, List<TimeSlot> freeTimeSlotsToCreate) {
    super(occurredDateTime, personId);
    this.scheduledTimeSlot = scheduledTimeSlot;
    this.freeTimeSlotToDelete = freeTimeSlotToDelete;
    this.freeTimeSlotsToCreate = freeTimeSlotsToCreate;
  }

  public static EntryScheduledEvent Create(OffsetDateTime occurredDateTime,
      Id personId, TimeSlot scheduledTimeSlot, TimeSlot freeTimeSlotToDelete,
      List<TimeSlot> freeTimeSlotsToCreate) {
    if (scheduledTimeSlot.getEntryId() == null) {
      System.out.println("Scheduled TimeSlot can't have a null entryId.");
      exit(-9);
    }
    EntryScheduledEvent entryScheduledEvent =
        new EntryScheduledEvent(occurredDateTime, personId, scheduledTimeSlot,
            freeTimeSlotToDelete,
            freeTimeSlotsToCreate);
    return entryScheduledEvent;
  }

  public Id getEntryId() {
    return scheduledTimeSlot.getEntryId();
  }

  public TimeSlot getScheduledTimeSlot() {
    return scheduledTimeSlot;
  }

  public TimeSlot getFreeTimeSlotToDelete() {
    return freeTimeSlotToDelete;
  }

  public List<TimeSlot> getFreeTimeSlotsToCreate() {
    return freeTimeSlotsToCreate;
  }

  public String getEventType() {
    return eventType;
  }
}
