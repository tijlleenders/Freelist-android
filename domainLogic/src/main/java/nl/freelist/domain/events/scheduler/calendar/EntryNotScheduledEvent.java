package nl.freelist.domain.events.scheduler.calendar;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryNotScheduledEvent extends Event {

  private String eventType = "EntryScheduledEvent";
  private Id entryId;

  private EntryNotScheduledEvent(
      OffsetDateTime occurredDateTime, Id personId, Id entryId) {
    super(occurredDateTime, personId);
    this.entryId = entryId;
  }

  public static EntryNotScheduledEvent Create(OffsetDateTime occurredDateTime,
      Id personId, Id entryId) {
    if (entryId == null) {
      System.out.println("Scheduled TimeSlot can't have a null entryId.");
      exit(-9);
    }
    EntryNotScheduledEvent entryScheduledEvent =
        new EntryNotScheduledEvent(occurredDateTime, personId, entryId);
    return entryScheduledEvent;
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getEventType() {
    return eventType;
  }
}
