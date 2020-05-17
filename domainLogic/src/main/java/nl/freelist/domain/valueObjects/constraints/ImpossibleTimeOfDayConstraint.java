package nl.freelist.domain.valueObjects.constraints;

import java.time.OffsetDateTime;
import nl.freelist.domain.valueObjects.TimeSlot;

public final class ImpossibleTimeOfDayConstraint extends Constraint {

  private final String timeOfDay;

  private ImpossibleTimeOfDayConstraint(String timeOfDay) {
    super();
    this.timeOfDay = timeOfDay;
  }

  public static ImpossibleTimeOfDayConstraint Create(String timeOfDay) {
    return new ImpossibleTimeOfDayConstraint(timeOfDay);
  }

  @Override
  public Boolean validate(TimeSlot timeSlot) {
    OffsetDateTime pointer = timeSlot.getStartDateTime();
    while (pointer.isBefore(timeSlot.getEndDateTime())) {
//          if(pointer.getDayOfWeek() == timeOfDay) { // Todo: replace ENUM?
      return true;
    }
    pointer = pointer.plusDays(1);
//        }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (!o.getClass().getSimpleName().equals("ImpossibleTimeOfDayConstraint")) {
      return false;
    }
    ImpossibleTimeOfDayConstraint other = (ImpossibleTimeOfDayConstraint) o;
    if (this.timeOfDay.equals(other.timeOfDay)
    ) {
      return true;
    }
    return super.equals(o);
  }

  @Override
  public String toString() {
    return timeOfDay;
  }

}
