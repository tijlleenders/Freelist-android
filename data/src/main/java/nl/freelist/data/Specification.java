package nl.freelist.data;

import nl.freelist.domain.interfaces.Specifiable;

public class Specification implements Specifiable {

  private String domainSpecification;

  public Specification(String domainSpecification) {
    this.domainSpecification = domainSpecification;
  }

  public String makeDataSpecification() {
    return null;
  }
}
