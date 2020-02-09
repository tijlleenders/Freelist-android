package nl.freelist.domain.valueObjects;

import static java.lang.System.exit;

import java.time.OffsetDateTime;
import java.util.List;

public final class DtrConstraint {

  private enum ConstraintType {
    OR,
    AND,
    OPENBRACKET,
    CLOSEBRACKET,
    STARTSATORAFTER,
    DUEBEFORE,
    NOMONDAYS,
    NOTUESDAYS,
    NOWEDNESDAYS,
    NOTHURSDAYS,
    NOFRIDAYS,
    NOSATURDAYS,
    NOSUNDAYS,
    NOMORNINGS,
    NOAFTERNOONS,
    NOEVENINGS,
    NONIGHTS
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

  public static List<DtrConstraint> Simplify(List<DtrConstraint> toSimplify) {
    if (!toSimplify.contains(DtrConstraint.Create("OR", null))) {
      toSimplify.removeIf(n -> n.constraintType.equals(ConstraintType.OPENBRACKET));
      toSimplify.removeIf(n -> n.constraintType.equals(ConstraintType.CLOSEBRACKET));
    }
    return toSimplify;
  }

  @Override
  public boolean equals(Object o) {
    if (!o.getClass().getSimpleName().equals("DtrConstraint")) {
      return false;
    }
    DtrConstraint other = (DtrConstraint) o;
    if (this.constraintType.equals(other.constraintType)
        && this.offsetDateTime == other.offsetDateTime
    ) {
      return true;
    }
    return super.equals(o);
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
