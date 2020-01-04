package nl.freelist.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.freelist.domain.events.EntryScheduledEvent;
import nl.freelist.domain.events.Event;
import nl.freelist.domain.events.ResourceCreatedEvent;
import nl.freelist.domain.valueObjects.Appointment;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.Email;

public class Resource {

  private static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

  private Email ownerEmail;
  private Email resourceEmail;
  private UUID uuid;
  private int lastAppliedEventSequenceNumber; //Todo: apply resourceCreatedEvent
  private List<Event> eventList = new ArrayList<>();
  private DateTimeRange lifetimeDateTimeRange;
  private Calendar calendar;

  private Resource(
      Email ownerEmail,
      Email resourceEmail,
      DateTimeRange lifetimeDateTimeRange
  ) {
    this.ownerEmail = ownerEmail;
    this.resourceEmail = resourceEmail;
    uuid = UUID.randomUUID();
    lastAppliedEventSequenceNumber = -1;
    this.lifetimeDateTimeRange = lifetimeDateTimeRange;
    calendar = Calendar
        .Create(
            null,
            uuid,
            lastAppliedEventSequenceNumber,
            -1,
            lifetimeDateTimeRange,
            null,
            -1);
    LOGGER.log(Level.INFO,
        "Resource " + uuid.toString() + " created with lastAppliedEventSequenceNumber "
            + lastAppliedEventSequenceNumber);
  }

  public static Resource Create(
      Email ownerEmail,
      Email resourceEmail,
      DateTimeRange lifetimeDateTimeRange
  ) {
    //Todo: validation as static method?
    return new Resource(ownerEmail, resourceEmail, lifetimeDateTimeRange);
  }


  public List<Calendar> getSchedulingOptions(Entry entry) {
    List<Calendar> calendarOptionList = new ArrayList<>();
    List<Appointment> currentAppointmentList = calendar.getAppointments();
    int currentAppointmentListSize;
    if (currentAppointmentList == null) {
      currentAppointmentListSize = 0;
    } else {
      currentAppointmentListSize = currentAppointmentList.size();
    }
    //loop through all possible prio positions
    for (int prio = 0; prio < currentAppointmentListSize + 1; prio++) {
      LOGGER.log(Level.INFO, "prio loop:" + prio);
      //Add appointment to prio position corresponding to loop
      Appointment appointmentToSchedule;
      appointmentToSchedule = Appointment
          .Create(prio, entry.getUuid(), entry.getDuration(), false, null);
      List<Appointment> tempAppointmentList = new ArrayList<>();
      if (currentAppointmentList != null) {
        tempAppointmentList.addAll(currentAppointmentList);
      }
      tempAppointmentList.add(prio, appointmentToSchedule);
      Calendar calendarOption;
      calendarOption = Calendar.Create(
          tempAppointmentList,
          uuid,
          lastAppliedEventSequenceNumber,
          entry.getLastAppliedEventSequenceNumber(),
          lifetimeDateTimeRange,
          calendar,
          prio
      );
      calendarOptionList.add(calendarOption);
    }
    return calendarOptionList;
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
      case "ResourceCreatedEvent":
        LOGGER.log(Level.INFO,
            "ResourceCreatedEvent applied to resource");
        ResourceCreatedEvent resourceCreatedEvent = (ResourceCreatedEvent) event;
        this.uuid = UUID.fromString(resourceCreatedEvent.getAggregateId());
        this.ownerEmail = resourceCreatedEvent.getOwnerEmail();
        this.resourceEmail = resourceCreatedEvent.getResourceEmail();
        this.lifetimeDateTimeRange = resourceCreatedEvent.getLifetimeDateTimeRange();
        break;
      case "EntryScheduledEvent":
        LOGGER.log(Level.INFO,
            "EntryScheduledEvent applied to resource");
        EntryScheduledEvent entryScheduledEvent = (EntryScheduledEvent) event;
        if (!entryScheduledEvent.getResourceUuid().equals(uuid.toString())) {
          LOGGER.log(Level.SEVERE, "EntryScheduledEvent can't be applied to resource.");
          break;
        }
        calendar = entryScheduledEvent.getCalendar();
        break;
      default:
        LOGGER.log(Level.SEVERE,
            "Event can't be applied to entry " + uuid.toString() + " ; event type not recognized");
        break;
    }
    eventList.add(event);
    lastAppliedEventSequenceNumber += 1;

  }

  public DateTimeRange getLifetimeDateTimeRange() {
    return lifetimeDateTimeRange;
  }

  public Email getOwnerEmail() {
    return ownerEmail;
  }

  public Email getResourceEmail() {
    return resourceEmail;
  }

  public UUID getUuid() {
    return uuid;
  }

  public int getLastAppliedEventSequenceNumber() {
    return lastAppliedEventSequenceNumber;
  }

  public List<Event> getEventList() {
    return eventList;
  }

  public Calendar getCalendar() {
    return calendar;
  }
}
