package kr.co.plasticcity.jmata.function;

@FunctionalInterface
public interface JMSupplier<R>
{
	R get();
}