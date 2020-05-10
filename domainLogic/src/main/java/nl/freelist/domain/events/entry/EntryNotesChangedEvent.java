package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;

public class EntryNotesChangedEvent extends Event {

  private String notesAfter;
  private String eventType = "EntryNotesChangedEvent";

  private EntryNotesChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      String notesAfter
  ) {
    super(occurredDateTime, entryId);
    this.notesAfter = notesAfter;
  }

  public static EntryNotesChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      String descriptionAfter
  ) {
    return new EntryNotesChangedEvent(
        occurredDateTime,
        entryId,
        descriptionAfter
    );
  }

  public String getNotesAfter() {
    return notesAfter;
  }

  public String getEventType() {
    return eventType;
  }
}
