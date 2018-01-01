package kr.co.plasticcity.jmata.function;

/**
 * Created by JongsunYu on 2018-01-01.
 */

@FunctionalInterface
public interface Function<T, R>
{
	R apply(final T t);
}
