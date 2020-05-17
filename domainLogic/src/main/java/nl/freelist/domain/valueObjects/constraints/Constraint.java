package nl.freelist.domain.valueObjects.constraints;

import static java.lang.System.exit;

import java.time.DayOfWeek;
import nl.freelist.domain.valueObjects.TimeSlot;

public abstract class Constraint { // use abstract class so custom and/or compositions of constraints can be made later

  //Todo: implement Constraint composition in domain and UI
  public static Constraint fromString(String fromString) {
    String type = Constraint.class.getSimpleName();
    switch (type) {
      case "ImpossibleTimeOfDayConstraint":
        return ImpossibleTimeOfDayConstraint.Create(fromString);
      case "ImpossibleDaysConstraint":
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(fromString);
        return ImpossibleDaysConstraint.Create(dayOfWeek);
      default:
        exit(-9);
    }
    return null;
  }

  public abstract Boolean validate(TimeSlot timeSlot);

  @Override
  public abstract String toString();
}
