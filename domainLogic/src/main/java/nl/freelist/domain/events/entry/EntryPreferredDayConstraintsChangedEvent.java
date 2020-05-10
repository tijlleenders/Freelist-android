package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import java.util.List;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.DtrConstraint;
import nl.freelist.domain.valueObjects.Id;

public class EntryPreferredDayConstraintsChangedEvent extends Event {

  private List<DtrConstraint> preferredDayConstraints;
  private String eventType = "EntryPreferredDayConstraintsChangedEvent";

  private EntryPreferredDayConstraintsChangedEvent(
      OffsetDateTime occurredDateTime,
      Id entryId,
      List<DtrConstraint> preferredDayConstraints) {
    super(occurredDateTime, entryId);
    this.preferredDayConstraints = preferredDayConstraints;
  }

  public static EntryPreferredDayConstraintsChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id entryId,
      List<DtrConstraint> preferredDayConstraints) {

    return new EntryPreferredDayConstraintsChangedEvent(
        occurredDateTime, entryId, preferredDayConstraints);
  }

  public String getEventType() {
    return eventType;
  }

  public List<DtrConstraint> getPreferredDayConstraints() {
    return preferredDayConstraints;
  }
}
