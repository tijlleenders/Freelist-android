package nl.freelist.domain.useCases;

import nl.freelist.domain.crossCuttingConcerns.ResultObject;
import nl.freelist.domain.crossCuttingConcerns.ThreadLogger;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.useCaseTypes.UseCaseWithParameter;

public class SaveEntryUseCase implements UseCaseWithParameter<Entry, ResultObject<Entry>> {

  private Repository<Entry> entryRepository;

  public SaveEntryUseCase(Repository<Entry> entryRepository) {
    this.entryRepository = entryRepository;
  }

  @Override
  public ResultObject<Entry> execute(Entry entry) {
    ThreadLogger.logThreadSignature("ThreadLogger: Inside saveEntry use case.");
    return entryRepository.insert(entry);
  }
}
