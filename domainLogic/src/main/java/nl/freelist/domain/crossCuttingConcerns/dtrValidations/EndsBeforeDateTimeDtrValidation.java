package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;
import nl.freelist.domain.valueObjects.DtrConstraint;

public class EndsBeforeDateTimeDtrValidation extends DtrValidation {

  private OffsetDateTime endDueDateTime;

  EndsBeforeDateTimeDtrValidation(OffsetDateTime endDueDateTime) {
    this.endDueDateTime = endDueDateTime;
  }

  public Result validate(DateTimeRange dateTimeRange) {
    if (dateTimeRange.getEndDateTime().isBefore(endDueDateTime)) {
      Result result = Result.Create(true, null, "", "");
      return result;
    } else {
      Result result = Result.Create(false, null, "", "");
      return result;
    }

  }

  @Override
  public List<DtrConstraint> list() {
    DtrConstraint dtrConstraint = DtrConstraint.Create("DUEBEFORE", endDueDateTime);
    List<DtrConstraint> dtrConstraintList = new ArrayList<>();
    dtrConstraintList.add(dtrConstraint);
    return dtrConstraintList;
  }

  public OffsetDateTime getEndDueDateTime() {
    return endDueDateTime;
  }
}
