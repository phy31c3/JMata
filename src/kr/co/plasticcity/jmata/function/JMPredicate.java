package kr.co.plasticcity.jmata.function;

@FunctionalInterface
public interface JMPredicate<T>
{
	boolean test(T t);
}