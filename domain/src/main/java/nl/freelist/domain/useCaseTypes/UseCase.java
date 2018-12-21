package nl.freelist.domain.useCaseTypes;

public interface UseCase<T> {

  T execute();
}