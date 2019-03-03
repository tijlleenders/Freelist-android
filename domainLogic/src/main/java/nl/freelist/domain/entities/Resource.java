package nl.freelist.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.valueObjects.Email;

public class Resource {

  private static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

  private Email email;
  private String name;
  private String uuid;
  private int lastAppliedEventSequenceNumber;
  private List<Event> eventList = new ArrayList<>();

  private Resource(Email email, String name) {
    //Todo: validation as static method?
    this.email = email;
    this.name = name;
    uuid = UUID.randomUUID().toString();
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(Level.INFO,
        "Resource " + uuid.toString() + " created with lastAppliedEventSequenceNumber "
            + lastAppliedEventSequenceNumber);
  }

  public Resource Create(Email email, String name) {
    return new Resource(email, name);
  }

}
