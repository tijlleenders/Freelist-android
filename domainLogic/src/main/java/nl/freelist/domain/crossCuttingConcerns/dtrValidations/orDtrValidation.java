package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;

public class orDtrValidation extends DtrValidation {

  DtrValidation validation1;
  DtrValidation validation2;

  orDtrValidation(DtrValidation validation1, DtrValidation validation2) {
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

}
