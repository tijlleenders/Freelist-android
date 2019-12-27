package nl.freelist.domain.crossCuttingConcerns;

import java.util.List;
import nl.freelist.domain.valueObjects.ValidationError;

public class Result { //should this be a value object?

  private List<ValidationError> validationErrorList;
  private boolean isSuccess;
  private String parameters;
  private String message;

  private Result(boolean isSuccess, List<ValidationError> validationErrorList
      , String parameters, String message) {
    this.isSuccess = isSuccess;
    this.validationErrorList = validationErrorList;
    this.parameters = parameters;
    this.message = message;
  }

  public static Result Create(boolean isSuccess, List<ValidationError> validationErrorList
      , String parameters, String message) {
    Result result;
    result = new Result(isSuccess, validationErrorList, parameters, message);
    return result;
  }

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
