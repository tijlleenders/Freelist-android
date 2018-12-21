package nl.freelist.domain.useCaseTypes;

public interface UseCaseWithParameter<P, R> {

  R execute(P parameter);
}
