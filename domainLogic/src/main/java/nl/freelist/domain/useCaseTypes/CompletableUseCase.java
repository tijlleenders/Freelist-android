package nl.freelist.domain.useCaseTypes;

import io.reactivex.Completable;

public interface CompletableUseCase {

  Completable execute();
}
