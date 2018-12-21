package nl.freelist.domain.useCases;

import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.useCaseTypes.UseCaseWithParameter;

public class GetEntryByIdUseCase implements UseCaseWithParameter<Integer, Entry> {

  private Repository<Entry> entryRepository;

  public GetEntryByIdUseCase(Repository<Entry> entryRepository) {
    this.entryRepository = entryRepository;
  }

  @Override
  public Entry execute(Integer id) {
    return entryRepository.getById(id);
  }
}
