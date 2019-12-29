package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public abstract class DtrValidation {

  public abstract Result validate(DateTimeRange dateTimeRange);

  public abstract List<DtrConstraint> list();

}
