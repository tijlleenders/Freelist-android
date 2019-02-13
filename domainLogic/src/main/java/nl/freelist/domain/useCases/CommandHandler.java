package nl.freelist.domain.useCases;


import nl.freelist.domain.commands.Command;
import nl.freelist.domain.crossCuttingConcerns.Result;
import nl.freelist.domain.crossCuttingConcerns.ThreadLogger;
import nl.freelist.domain.useCaseTypes.UseCaseWithParameter;

public class CommandHandler implements UseCaseWithParameter<Command, Result> {

  public CommandHandler() {
  }

  @Override
  public Result execute(Command command) {
    ThreadLogger.logThreadSignature("ThreadLogger: Inside CommandHandler use case.");
    return command.execute();
  }
}
