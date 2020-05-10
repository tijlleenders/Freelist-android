package nl.freelist.domain.valueObjects;

import java.util.UUID;

public final class Id {

  private String id;

  private Id() {
    //Todo: validation as static method?
    this.id = UUID.randomUUID().toString();
    ;
  }

  public static Id Create() {
    //Validation logic
    return new Id();
  }

}
