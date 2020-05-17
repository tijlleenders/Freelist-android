package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryNotesChangedEvent extends Event {

  private Id entryId;
  private String notesAfter;
  private String eventType = "EntryNotesChangedEvent";

  private EntryNotesChangedEvent(
      OffsetDateTime occurredDateTime,
      Id personId,
      Id entryId,
      String notesAfter
  ) {
    super(occurredDateTime, personId);
    this.notesAfter = notesAfter;
    this.entryId = entryId;
  }

  public static EntryNotesChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id personId,
      Id entryId,
      String descriptionAfter
  ) {
    return new EntryNotesChangedEvent(
        occurredDateTime,
        personId,
        entryId,
        descriptionAfter
    );
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getNotesAfter() {
    return notesAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
