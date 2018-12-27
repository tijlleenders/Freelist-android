package nl.freelist.domain.useCaseTypes;

import io.reactivex.Single;

public interface SingleUseCase<T> {

  Single<T> execute();


}
