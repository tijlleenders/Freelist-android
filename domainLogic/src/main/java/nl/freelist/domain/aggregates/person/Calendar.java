package nl.freelist.domain.aggregates.person;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.person.calendar.CalendarCreatedEvent;
import nl.freelist.domain.valueObjects.Appointment;
import nl.freelist.domain.valueObjects.DateTimeRange;

public class Calendar { // Calendar has a history you want to track so it can't be a value object

  // , it's an entity (with no visibility on outside? Should it have events?) - or is it just
  // internal part of Person...?
  private static final Logger LOGGER = Logger.getLogger(Calendar.class.getName());

  private List<Event> eventList = new ArrayList<>();
  private List<Appointment> appointmentList = new ArrayList<>();
  private List<DateTimeRange> freeDateTimeRanges = new ArrayList<>();
  private String calendarId;
  private int lastAppliedEventSequenceNumber;

  private Calendar() {
    // Todo: move logic to method in Person?
    lastAppliedEventSequenceNumber = -1;
    LOGGER.log(
        Level.INFO,
        "Calendar initiated without CreateCalendar event.");
  }

  public static Calendar Create() {
    // Do checking
    return new Calendar();
  }

  public void applyEvent(Event event) {
    // Todo: maybe move every applyEvent to it's own function with subclass parameter?

    if (event == null) {
      return;
    }
    String eventClass = event.getClass().getSimpleName();
    switch (eventClass) {
      case "CalendarCreatedEvent":
        if (lastAppliedEventSequenceNumber != -1) {
          LOGGER
              .log(Level.WARNING, "CalendarCreatedEvent applied to Calendar that already exists!");
          break;
        }
        CalendarCreatedEvent calendarCreatedEvent = (CalendarCreatedEvent) event;
        this.calendarId = calendarCreatedEvent.getAggregateId();
        eventList.add(event);
        lastAppliedEventSequenceNumber += 1;
        LOGGER.log(Level.INFO, "CalendarCreatedEvent applied");
        break;
      default:
        LOGGER.log(
            Level.WARNING,
            "Event can't be applied to calendar " + calendarId + " ; event type not recognized");
        break;
    }
  }

  public List<Event> getListOfEventsWithSequenceHigherThan(int fromEventSequenceNumber) {
    fromEventSequenceNumber += 1;
    return eventList.subList(fromEventSequenceNumber, eventList.size());
  }

  public void applyEvents(List<Event> eventList) {
    for (Event event : eventList) {
      applyEvent(event);
    }
  }

  public int getLastAppliedEventSequenceNumber() {
    return lastAppliedEventSequenceNumber;
  }

}
