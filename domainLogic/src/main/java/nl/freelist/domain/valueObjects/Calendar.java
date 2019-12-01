package nl.freelist.domain.valueObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Calendar {

  private final List<Appointment> appointmentList;
  private final UUID resourceUuid;
  private final int resourceLastAppliedEventSequenceNumber;
  private final int entryLastAppliedEventSequenceNumber;
  private final int lastScheduledAppointmentPosition;
  private final DateTimeRange entryLastScheduledDateTimeRange;
  private final int numberOfProblems;
  private final int numberOfReschedules;


  private Calendar(List<Appointment> appointmentList, UUID resourceUuid,
      int resourceLastAppliedEventSequenceNumber, int entryLastAppliedEventSequenceNumber,
      DateTimeRange resourceLifetimeDateTimeRange, Calendar calendarToCompareWith,
      int lastScheduledAppointmentPosition) {
    this.appointmentList = appointmentList;
    this.resourceUuid = resourceUuid;
    this.resourceLastAppliedEventSequenceNumber = resourceLastAppliedEventSequenceNumber;
    this.entryLastAppliedEventSequenceNumber = entryLastAppliedEventSequenceNumber;
    this.lastScheduledAppointmentPosition = lastScheduledAppointmentPosition;
    this.numberOfProblems = 0;
    this.numberOfReschedules = 0;
    //Do the logic
    List<DateTimeRange> freeDateTimeRanges = new ArrayList<>();
    freeDateTimeRanges.add(resourceLifetimeDateTimeRange);
    int appointmentListSize;
    if (appointmentList != null) { //To guard for first creation of the Calendar within Resource
      appointmentListSize = appointmentList.size();
      int freeDateTimeRangesSize;
      if (freeDateTimeRanges == null) {
        freeDateTimeRangesSize = 0;
      } else {
        freeDateTimeRangesSize = freeDateTimeRanges.size();
      }

      for (int appointmentPosition = 0; appointmentPosition < appointmentListSize;
          appointmentPosition++) {
        boolean noSlotFound = true;
        for (int freeDateTimeRangePosition = 0;
            freeDateTimeRangePosition < freeDateTimeRangesSize; freeDateTimeRangePosition++) {
          if (appointmentListSize == 0) {
            // do nothing
          } else {
            if (freeDateTimeRanges.get(freeDateTimeRangePosition).getDuration() > appointmentList
                .get(appointmentPosition).getDuration()) {
              //Yes, it fits, so schedule it
              DateTimeRange scheduledDateTimeRange = DateTimeRange.Create(
                  freeDateTimeRanges.get(freeDateTimeRangePosition).getStartDateTime(),
                  freeDateTimeRanges.get(freeDateTimeRangePosition).getStartDateTime()
                      .plusSeconds(appointmentList.get(appointmentPosition).getDuration()));
              //replace Appointment with new one that has scheduledDTR
              appointmentList.add(appointmentPosition, Appointment.Create(
                  appointmentPosition, appointmentList.get(appointmentPosition).getEntryId(),
                  appointmentList.get(appointmentPosition).getDuration(),
                  false,
                  scheduledDateTimeRange
                  )
              );
              appointmentList.remove(appointmentPosition + 1);
              //reduce freeTime ranges
              freeDateTimeRanges.add(  //Todo: replace instead of add+remove
                  freeDateTimeRangePosition,
                  DateTimeRange.Create(
                      freeDateTimeRanges.get(freeDateTimeRangePosition).getStartDateTime()
                          .plusSeconds(appointmentList.get(appointmentPosition).getDuration()),
                      freeDateTimeRanges.get(freeDateTimeRangePosition).getEndDateTime()
                  )
              );
              freeDateTimeRanges.remove(freeDateTimeRangePosition + 1);
              noSlotFound = false;
            }
          }
        }
        if (noSlotFound) {
          appointmentList.add(appointmentPosition, Appointment.Create(
              appointmentPosition, appointmentList.get(appointmentPosition).getEntryId(),
              appointmentList.get(appointmentPosition).getDuration(),
              false,
              null
              )
          );
          appointmentList.remove(appointmentPosition + 1);
        }
      }
    }
    if (appointmentList != null
        && appointmentList.get(lastScheduledAppointmentPosition).getScheduledDTR() != null) {
      entryLastScheduledDateTimeRange = appointmentList.get(lastScheduledAppointmentPosition)
          .getScheduledDTR();
    } else {
      entryLastScheduledDateTimeRange = null;
    }
  }

  public static Calendar Create(List<Appointment> appointmentList, UUID resourceUuid,
      int resourceLastAppliedEventSequenceNumber,
      int entryLastAppliedEventSequenceNumber,
      DateTimeRange resourceLifetimeDateTimeRange, Calendar calendarToCompareWith,
      int lastScheduledAppointmentPosition
  ) {
    //Do checking
    return new Calendar(appointmentList, resourceUuid, resourceLastAppliedEventSequenceNumber,
        entryLastAppliedEventSequenceNumber,
        resourceLifetimeDateTimeRange,
        calendarToCompareWith,
        lastScheduledAppointmentPosition);
  }

  public int getNumberOfProblems() {
    return numberOfProblems;
  }

  public int getNumberOfReschedules() {
    return numberOfReschedules;
  }

  public UUID getResourceUuid() {
    return resourceUuid;
  }

  public int getNumberOfAppointments() {
    if (appointmentList == null) {
      return 0;
    }
    return appointmentList.size();
  }

  public List<Appointment> getAppointments() {
    return appointmentList;
  }

  public int getResourceLastAppliedEventSequenceNumber() {
    return resourceLastAppliedEventSequenceNumber;
  }

  public String getLastScheduledEntryUuidString() {
    return appointmentList.get(lastScheduledAppointmentPosition).getEntryId().toString();
  }

  public DateTimeRange getEntryLastScheduledDateTimeRange() {
    return entryLastScheduledDateTimeRange;
  }

  public int getEntryLastAppliedEventSequenceNumber() {
    return entryLastAppliedEventSequenceNumber;
  }
}
