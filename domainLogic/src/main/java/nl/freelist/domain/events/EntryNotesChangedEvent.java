package nl.freelist.domain.events;

import java.time.OffsetDateTime;

public class EntryNotesChangedEvent extends Event {

  private String notesAfter;
  private String eventType = "EntryNotesChangedEvent";

  private EntryNotesChangedEvent(
      OffsetDateTime occurredDateTime,
      String entryId,
      String notesAfter
  ) {
    super(occurredDateTime, entryId);
    this.notesAfter = notesAfter;
  }

  public static EntryNotesChangedEvent Create(
      OffsetDateTime occurredDateTime,
      String entryId,
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
