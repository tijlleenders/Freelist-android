package nl.freelist.domain.commands;

import nl.freelist.domain.crossCuttingConcerns.Result;

public abstract class Command {

  public abstract Result execute();
}
