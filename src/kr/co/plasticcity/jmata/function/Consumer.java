package kr.co.plasticcity.jmata.function;

/**
 * Created by JongsunYu on 2018-01-01.
 */

@FunctionalInterface
public interface Consumer<T>
{
	void accept(final T t);
}
