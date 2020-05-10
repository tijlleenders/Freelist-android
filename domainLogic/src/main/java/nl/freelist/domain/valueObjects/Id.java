package nl.freelist.domain.valueObjects;

import java.util.UUID;

public final class Id {

  private String id;

  private Id() {
    // Todo: validation as static method?
    this.id = UUID.randomUUID().toString();
    ;
  }

  public static Id Create() {
    // Validation logic
    return new Id();
  }

  public static Id fromString(String idString) {
    if (!validate(idString)) {
      System.exit(-1);
    }
    Id id = Id.Create();
    id.id = idString;
    return id;
  }

  private static boolean validate(String idString) {
    try {
      UUID.fromString(idString);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public String toString() {
    return id;
  }
}
