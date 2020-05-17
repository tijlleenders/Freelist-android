package nl.freelist.domain.valueObjects.constraints;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import nl.freelist.domain.valueObjects.TimeSlot;

public final class ImpossibleDaysConstraint extends Constraint {

  private final DayOfWeek dayOfWeek;

  private ImpossibleDaysConstraint() {
    this.dayOfWeek = DayOfWeek.MONDAY; // I put this in to avoid 'may have been uninitialized' message in IDE - GSON will override final by reflection
  }

  private ImpossibleDaysConstraint(DayOfWeek dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public static ImpossibleDaysConstraint Create(DayOfWeek dayOfWeek) {
    return new ImpossibleDaysConstraint(dayOfWeek);
  }

  @Override
  public Boolean validate(TimeSlot timeSlot) {
    OffsetDateTime pointer = timeSlot.getStartDateTime();
    while (pointer.isBefore(timeSlot.getEndDateTime())) {
      if (pointer.getDayOfWeek() == dayOfWeek) { // Todo: replace ENUM?
        return false;
      }
      pointer = pointer.plusDays(1);
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (!o.getClass().getSimpleName().equals("ImpossibleDaysConstraint")) {
      return false;
    }
    ImpossibleDaysConstraint other = (ImpossibleDaysConstraint) o;
    if (this.dayOfWeek.equals(other.dayOfWeek)
    ) {
      return true;
    }
    return super.equals(o);
  }

  @Override
  public String toString() {
    return "NO" + dayOfWeek.toString() + "S";
  }

}
