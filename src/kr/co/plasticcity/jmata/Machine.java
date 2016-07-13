package kr.co.plasticcity.jmata;

public interface Machine
{
	StateAdder defineState(String name);
	
	public interface StateAdder
	{
		<T> To<T> when(T signal);
		
		void summit();
	}
	
	public interface To<T>
	{
		StateAdder to(String name);
	}
}