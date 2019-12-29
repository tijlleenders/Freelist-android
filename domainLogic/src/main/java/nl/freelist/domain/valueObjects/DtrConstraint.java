package nl.freelist.domain.valueObjects;

import static java.lang.System.exit;

import java.time.OffsetDateTime;

public final class DtrConstraint {

  private enum ConstraintType {
    OPERATOROR,
    OPERATORAND,
    OPENBRACKET,
    CLOSEBRACKET,
    DUEBEFORE,
    NOMONDAYS,
    NOTUESDAYS,
    NOWEDNESDAYS,
    NOTHURSDAYS,
    NOFRIDAYS,
    NOSUNDAYS,
    NOMORNINGS,
    NOAFTERNOONS,
    NOEVENINGS
  }

  private ConstraintType constraintType;
  private OffsetDateTime offsetDateTime;

  private DtrConstraint(String dtrConstraint, OffsetDateTime offsetDateTime) {
    this.constraintType = ConstraintType.valueOf(dtrConstraint);
    this.offsetDateTime = offsetDateTime;
  }

  public static DtrConstraint Create(String dtrConstraint, OffsetDateTime offsetDateTime) {
    try {
      ConstraintType constraintType = ConstraintType.valueOf(dtrConstraint);
    } catch (Exception e) {
      exit(-1);
    }
    return new DtrConstraint(dtrConstraint, offsetDateTime);
  }

  @Override
  public String toString() {
    return constraintType.toString();
  }

  public ConstraintType getConstraintType() {
    return constraintType;
  }

  public OffsetDateTime getOffsetDateTime() {
    return offsetDateTime;
  }
}
