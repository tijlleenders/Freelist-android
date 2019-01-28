package nl.freelist.domain.crossCuttingConcerns;

import java.util.List;

public class Result {

  private List<ValidationError> validationErrorList;
  private boolean isSuccess;

  public Result(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  ;

  public boolean isSuccess() {
    return isSuccess;
  }

  public List<ValidationError> getErrors() {
    return validationErrorList;
  }

  public String getMessage() {
    return "Message from resultObject."; // TODO: implement
  }

  public String getParameters() {
    // Error results include the parameters that were submitted.
    // This can be useful during Transparent Redirects to repopulate your form if validations fail.
    return "{transaction[amount]=1000.00, transaction[type]=sale, transaction[credit_card][expiration_date]=05/2012}";
  }
}
