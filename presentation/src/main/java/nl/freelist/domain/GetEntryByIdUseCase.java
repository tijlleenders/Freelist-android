package nl.freelist.domain;

import android.arch.lifecycle.LiveData;

import nl.freelist.repository.EntryRepository;
import nl.freelist.repository.ViewModelEntry;

public class GetEntryByIdUseCase implements
    UseCase<Integer, LiveData<ViewModelEntry>> { //Todo: create abstract base class for UseCase that makes sure it is always executed on background thread (non-UI-blocking)

  private final EntryRepository entryRepository;

  public GetEntryByIdUseCase(EntryRepository entryRepository) {
    this.entryRepository = entryRepository;
  }

  @Override
  public void execute(Integer parameter, Callback<LiveData<ViewModelEntry>> callback) {
    try {
      callback.onSuccess(entryRepository.getViewModelEntry(parameter));
    } catch (final Throwable throwable) {
      callback.onError(throwable);
    }
  }
}
