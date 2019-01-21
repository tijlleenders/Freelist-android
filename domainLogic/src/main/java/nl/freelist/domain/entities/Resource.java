package nl.freelist.domain.entities;

import nl.freelist.domain.valueObjects.Email;

public class Resource {

  private Email email;
  private String name;

  Resource(Email email, String name) {
    //Todo: validation as static method?
    this.email = email;
    this.name = name;
  }


}
