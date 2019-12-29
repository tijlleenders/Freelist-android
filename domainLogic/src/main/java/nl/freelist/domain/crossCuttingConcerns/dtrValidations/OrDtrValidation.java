package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class OrDtrValidation extends DtrValidation {

  DtrValidation validation1;
  DtrValidation validation2;

  OrDtrValidation(DtrValidation validation1, DtrValidation validation2) {
    this.validation1 = validation1;
    this.validation2 = validation2;
  }

  public Result validate(DateTimeRange dateTimeRange) {
    if (validation1.validate(dateTimeRange).isSuccess() || validation2.validate(dateTimeRange)
        .isSuccess()) {
      Result result = Result.Create(true, null, "", "");
      return result;
    } else {
      Result result = Result.Create(false, null, "", "");
      return result;
    }
  }

  @Override
  public List<DtrConstraint> list() {
    List<DtrConstraint> dtrConstraintList = new ArrayList<>();
    dtrConstraintList.add(DtrConstraint.Create("OPENBRACKET", null));
    dtrConstraintList.addAll(validation1.list());
    dtrConstraintList.add(DtrConstraint.Create("OPERATOROR", null));
    dtrConstraintList.addAll(validation2.list());
    dtrConstraintList.add(DtrConstraint.Create("CLOSEBRACKET", null));
    return dtrConstraintList;
  }
}
