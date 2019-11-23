package nl.freelist.domain.events;

import nl.freelist.domain.valueObjects.DateTime;

public class ResourceCreatedEvent extends Event {

  private String ownerUuid;
  private String resourceUuid;

  private ResourceCreatedEvent(DateTime occurredDateTime, String ownerUuid, String resourceUuid,
      int eventSequenceNumber) {
    super(occurredDateTime, resourceUuid, eventSequenceNumber);
    this.ownerUuid = ownerUuid;
    this.resourceUuid = resourceUuid;
  }

  public static ResourceCreatedEvent Create(DateTime occurredDateTime, String ownerUuid,
      String resourceUuid, int eventSequenceNumber) {
    ResourceCreatedEvent resourceCreatedEvent = new ResourceCreatedEvent(occurredDateTime,
        ownerUuid,
        resourceUuid, eventSequenceNumber);
    return resourceCreatedEvent;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  public String getResourceUuid() {
    return resourceUuid;
  }

}
