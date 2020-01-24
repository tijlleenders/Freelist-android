package nl.freelist.domain.valueObjects;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class Appointment {

  private final int prio;
  private final UUID entryId;
  private final long duration;
  private final DateTimeRange scheduledDTR;
  private final boolean isDone;

  private Appointment(int prio, UUID entryId, long duration, boolean isDone,
      DateTimeRange scheduledDTR) {
    this.prio = prio;
    this.entryId = entryId;
    this.duration = duration;
    this.isDone = isDone;
    this.scheduledDTR = scheduledDTR;
  }

  public static Appointment Create(int prio, UUID entryId, long duration, boolean isDone,
      DateTimeRange scheduledDTR) {
    return new Appointment(prio, entryId, duration, isDone, scheduledDTR);
  }

  public int getPrio() {
    return prio;
  }

  public OffsetDateTime getStartDateTime() {
    return scheduledDTR.getStartDateTime(); //Todo: check if null problem
  }

  public OffsetDateTime getEndDateTime() {
    return scheduledDTR.getEndDateTime(); //Todo: check if null problem
  }

  public UUID getEntryId() {
    return entryId;
  }

  public boolean isDone() {
    return isDone;
  }

  public long getDuration() {
    return duration;
  }

  public DateTimeRange getScheduledDTR() {
    return scheduledDTR;
  }
}
