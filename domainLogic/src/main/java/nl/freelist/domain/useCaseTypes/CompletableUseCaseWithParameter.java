package nl.freelist.domain.useCaseTypes;

import io.reactivex.Completable;

public interface CompletableUseCaseWithParameter<P> {

  Completable execute(P parameter);
}
