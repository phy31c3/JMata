package kr.co.plasticcity.jmata.function;

public interface JMBiConsumer<T, U>
{
	void accept(T t, U u);
}