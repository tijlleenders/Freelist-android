package nl.freelist.domain;

public interface UseCase<P, R> {

  interface Callback<R> {

    void onSuccess(R returnValue);

    void onError(Throwable throwable);
  }

  void execute(P parameter, Callback<R> callback);
}