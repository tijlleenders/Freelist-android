package nl.freelist.domain.crossCuttingConcerns.dtrValidations;

import java.time.OffsetDateTime;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.valueObjects.DateTimeRange;

public class endsBeforeDateTimeDtrValidation extends DtrValidation {

  OffsetDateTime endDueDateTime;

  endsBeforeDateTimeDtrValidation(OffsetDateTime endDueDateTime) {
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
}
