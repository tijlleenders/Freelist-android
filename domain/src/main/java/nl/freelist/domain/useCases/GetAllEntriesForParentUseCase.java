package nl.freelist.domain.useCases;

import java.util.List;
import nl.freelist.domain.entities.Entry;
import nl.freelist.domain.interfaces.Repository;
import nl.freelist.domain.useCaseTypes.UseCaseWithParameter;

public class GetAllEntriesForParentUseCase implements UseCaseWithParameter<Integer, List<Entry>> {

  private Repository<Entry> entryRepository;

  public GetAllEntriesForParentUseCase(Repository<Entry> entryRepository) {
    this.entryRepository = entryRepository;
  }

  @Override
  public List<Entry> execute(Integer id) {
    List<Entry> result = entryRepository.getAllEntriesForParent(id);
    return result;
  }
}
