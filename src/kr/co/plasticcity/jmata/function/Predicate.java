package kr.co.plasticcity.jmata.function;

/**
 * Created by JongsunYu on 2018-01-01.
 */

@FunctionalInterface
public interface Predicate<T>
{
	boolean test(final T t);
}
