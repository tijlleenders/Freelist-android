package nl.freelist.domain.events.entry;

import java.time.OffsetDateTime;
import java.util.List;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Id;
import nl.freelist.domain.valueObjects.constraints.ImpossibleDaysConstraint;

public class EntryPreferredDayConstraintsChangedEvent extends Event {

  private Id entryId;
  private List<ImpossibleDaysConstraint> preferredDayConstraints;
  private String eventType = "EntryPreferredDayConstraintsChangedEvent";

  private EntryPreferredDayConstraintsChangedEvent(
      OffsetDateTime occurredDateTime,
      Id personId,
      Id entryId,
      List<ImpossibleDaysConstraint> preferredDayConstraints) {
    super(occurredDateTime, personId);
    this.preferredDayConstraints = preferredDayConstraints;
    this.entryId = entryId;
  }

  public static EntryPreferredDayConstraintsChangedEvent Create(
      OffsetDateTime occurredDateTime,
      Id personId,
      Id entryId,
      List<ImpossibleDaysConstraint> preferredDayConstraints) {

    return new EntryPreferredDayConstraintsChangedEvent(
        occurredDateTime, personId, entryId, preferredDayConstraints);
  }

  public Id getEntryId() {
    return entryId;
  }

  public String getEventType() {
    return eventType;
  }

  public List<ImpossibleDaysConstraint> getPreferredDayConstraints() {
    return preferredDayConstraints;
  }
}
