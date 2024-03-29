package nl.freelist.domain.aggregates;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.person.PersonCreatedEvent;
import nl.freelist.domain.valueObjects.Email;
import nl.freelist.domain.valueObjects.Id;

public class Person {

  private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

  private Id personId;
  private int lastAppliedEventSequenceNumber;
  private List<Event> eventList = new ArrayList<>();
  private List<Email> emails;

  private Person() {
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(
        Level.INFO,
        "Person initiated without any events applied.");
  }

  public static Person Create() {
    // Todo: validation as static method?
    return new Person();
  }

  public void applyEvents(List<Event> eventList) {
    for (Event event : eventList) {
      applyEvent(event);
    }
  }

  public List<Event> getListOfEventsWithSequenceHigherThan(int fromEventSequenceNumber) {
    fromEventSequenceNumber += 1;
    return eventList.subList(fromEventSequenceNumber, eventList.size());
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "PersonCreatedEvent":
        LOGGER.log(Level.INFO, "PersonCreatedEvent applied to person");
        PersonCreatedEvent personCreatedEvent = (PersonCreatedEvent) event;
        this.personId = personCreatedEvent.getAggregateId();
        break;
      default:
        LOGGER.log(
            Level.SEVERE,
            "Event can't be applied to person " + personId.toString()
                + " ; event type not recognized");
        break;
    }
    eventList.add(event);
    lastAppliedEventSequenceNumber += 1;
  }

  public Id getPersonId() {
    return personId;
  }

  public int getLastAppliedEventSequenceNumber() {
    return lastAppliedEventSequenceNumber;
  }

  public List<Event> getEventList() {
    return eventList;
  }

}
