package nl.freelist.domain.useCases;

import java.util.List;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.useCaseTypes.UseCase;

public class GetAllEntriesUseCase implements UseCase<List<Entry>> {

  private Repository<Entry> entryRepository;

  public GetAllEntriesUseCase(Repository<Entry> entryRepository) {
    this.entryRepository = entryRepository;
  }

  @Override
  public List<Entry> execute() {
    List<Entry> result = entryRepository.getAllEntries();
    return result;
  }
}
