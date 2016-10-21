package kr.co.plasticcity.jmata.function;

@FunctionalInterface
public interface JMBiConsumer<T, U>
{
	void accept(T t, U u);
}