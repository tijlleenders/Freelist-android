package nl.freelist.domain.valueObjects;

public class Email {

  private String email;

  public Email(String email) {
    //Todo: validation as static method?
    this.email = email;
  }

  public String getEmailString() {
    return email;
  }

}
