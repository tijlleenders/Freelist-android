package nl.freelist.domain.aggregates.person;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.entry.EntryScheduledEvent;
import nl.freelist.domain.events.person.PersonCreatedEvent;
import nl.freelist.domain.valueObjects.Email;
import nl.freelist.domain.valueObjects.Id;

public class Person {

  private static final Logger LOGGER = Logger.getLogger(Person.class.getName());

  private Email email;
  private Id personId;
  private int lastAppliedEventSequenceNumber;
  private List<Event> eventList = new ArrayList<>();
  private Calendar calendar;

  // Todo: add option for intelligent scheduling monte-carle reshuffle all prio positions

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
        calendar = Calendar.Create();
        break;
      case "EntryScheduledEvent": // Todo: one event can't be applied to two aggregates/entities
        LOGGER.log(Level.INFO, "EntryScheduledEvent applied to person");
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        if (!entryScheduledEvent.getPersonId().equals(personId.toString())) {
          LOGGER.log(Level.SEVERE, "EntryScheduledEvent can't be applied to person.");
          break;
        }
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

  public Email getEmail() {
    return email;
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
