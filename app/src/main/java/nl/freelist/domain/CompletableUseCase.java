package nl.freelist.domain;

public interface CompletableUseCase<P> {

  interface Callback {

    void onSuccess();

    void onError(Throwable throwable);
  }

  void execute(P parameter, Callback callback);
}
