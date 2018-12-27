package nl.freelist.domain.useCaseTypes;

import io.reactivex.Observable;

public interface ObservableUseCase<T> {

  Observable<T> execute();
}