package nl.freelist.domain.valueObjects;

public class ValidationError {

  private String errorMessage;

  ValidationError(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

}
