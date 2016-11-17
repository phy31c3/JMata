package kr.co.plasticcity.jmata.function;

@FunctionalInterface
public interface JMConsumer<T>
{
	void accept(T t);
}