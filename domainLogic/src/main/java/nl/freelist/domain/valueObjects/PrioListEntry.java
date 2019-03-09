package nl.freelist.domain.valueObjects;

public class PrioListEntry {

  private String entryId;
  private int priority;
  private boolean isScheduled;

  private PrioListEntry(String entryId, int priority, boolean isScheduled) {
    this.entryId = entryId;
    this.priority = priority;
    this.isScheduled = isScheduled;
  }

  public static PrioListEntry Create(String entryId, int priority, boolean isScheduled) {
    return new PrioListEntry(entryId, priority, isScheduled);
  }

}
