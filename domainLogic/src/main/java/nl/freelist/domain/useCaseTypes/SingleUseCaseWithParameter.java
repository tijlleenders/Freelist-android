package nl.freelist.domain.useCaseTypes;

import io.reactivex.Single;

public interface SingleUseCaseWithParameter<P, R> {

  Single<R> execute(P parameter);
}