package nl.freelist.domain.useCaseTypes;

import io.reactivex.Observable;

public interface ObservableUseCaseWithParameter<P, R> {

  Observable<R> execute(P parameter);
}
