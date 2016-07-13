package kr.co.plasticcity.jmata;

import java.util.function.*;

public interface JMata
{
	default Machine createMachine(String name)
	{
		// TODO
		return null;
	}
	
	default Machine getMachine(String name)
	{
		// TODO
		return null;
	}
	
	default void runMachine(String name)
	{
		// TODO
	}
	
	default void stopMachine(String name)
	{
		// TODO
	}
	
	default void terminateMachine(String name)
	{
		// TODO
	}
	
	default InputTo input(Object signal)
	{
		// TODO
		return null;
	}
	
	public interface InputTo
	{
		IfNull to(String machineName);
	}
	
	public interface IfNull
	{
		void ifNull(VoidConsumer f);
	}
}