package nl.freelist.repository;

abstract class Specification {

  private String domainSpecification;

  public Specification(String domainSpecification) {
    this.domainSpecification = domainSpecification;
  }

  abstract public String makeDataSpecification();
}
