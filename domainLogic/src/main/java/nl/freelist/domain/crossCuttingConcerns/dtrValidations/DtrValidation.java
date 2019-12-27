package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;

public abstract class DtrValidation {

  public abstract Result validate(DateTimeRange dateTimeRange);

}
