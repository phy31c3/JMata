package kr.co.plasticcity.jmata.function;

@FunctionalInterface
public interface JMFunction<T, R>
{
	R apply(T t);
}