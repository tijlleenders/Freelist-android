package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.DateHelper;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class NoSaturdaysDtrValidation extends DtrValidation {

  public Result validate(DateTimeRange dateTimeRange) {
    return DateHelper.checkIfDayNotPresentInDtr(dateTimeRange, "SATURDAY");
  }

  @Override
  public List<DtrConstraint> list() {
    DtrConstraint dtrConstraint = DtrConstraint.Create("NOSATURDAYS", null);
    List<DtrConstraint> dtrConstraintList = new ArrayList<>();
    dtrConstraintList.add(dtrConstraint);
    return dtrConstraintList;
  }

}
