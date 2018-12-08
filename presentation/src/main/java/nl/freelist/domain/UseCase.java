package nl.freelist.domain;

public interface UseCase<P, R> { //Todo: make R into generic Result object that can be extended per useCase, and has a value for a null response

  interface Callback<R> {

    void onSuccess(R returnValue);

    void onError(Throwable throwable);
  }

  void execute(P parameter, Callback<R> callback);
}