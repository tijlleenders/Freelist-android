package nl.freelist.domain.events;

import java.time.OffsetDateTime;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

public class ResourceCreatedEvent extends Event {

  private Email ownerEmail;
  private Email resourceEmail;
  private String ownerUuid;
  private DateTimeRange lifetimeDateTimeRange;
  private int eventSequenceNumber;
  private String eventType = "ResourceCreatedEvent";


  private ResourceCreatedEvent(
      OffsetDateTime occurredDateTime,
      Email ownerEmail,
      Email resourceEmail,
      String ownerUuid,
      String resourceUuid,
      DateTimeRange lifetimeDateTimeRange,
      int eventSequenceNumber) {
    super(occurredDateTime, resourceUuid);
    this.ownerEmail = ownerEmail;
    this.resourceEmail = resourceEmail;
    this.ownerUuid = ownerUuid;
    this.lifetimeDateTimeRange = lifetimeDateTimeRange;
    this.eventSequenceNumber = eventSequenceNumber;
  }

  public static ResourceCreatedEvent Create(
      OffsetDateTime occurredDateTime,
      Email ownerEmail,
      Email resourceEmail,
      String ownerUuid,
      String resourceUuid,
      DateTimeRange lifetimeDateTimeRange,
      int eventSequenceNumber) {
    ResourceCreatedEvent resourceCreatedEvent = new ResourceCreatedEvent(occurredDateTime,
        ownerEmail,
        resourceEmail,
        ownerUuid,
        resourceUuid,
        lifetimeDateTimeRange,
        eventSequenceNumber);
    return resourceCreatedEvent;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  public int getEventSequenceNumber() {
    return eventSequenceNumber;
  }

  public Email getOwnerEmail() {
    return ownerEmail;
  }

  public Email getResourceEmail() {
    return resourceEmail;
  }

  public DateTimeRange getLifetimeDateTimeRange() {
    return lifetimeDateTimeRange;
  }

  public String getEventType() {
    return eventType;
  }
}
