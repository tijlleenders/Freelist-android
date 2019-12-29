package nl.freelist.domain.valueObjects;

public final class Email {

  private String email;

  public Email(String email) {
    //Todo: validation as static method?
    this.email = email;
  }

  public String getEmailString() {
    return email;
  }

}
