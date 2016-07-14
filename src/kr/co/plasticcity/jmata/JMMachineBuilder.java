package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.JMMachine.*;

public interface JMMachineBuilder
{
	void addState(Class<?> state, Consumer<StateBuilder> func);
	
	public interface StateBuilder
	{
		StateBuilder setGroup(Class<?> group);
		
		<S> EnterWork<S> whenFrom(Class<S> signal);
		
		<S> SwitchTo<S> whenInput(Class<S> signal);
		
		void commit();
	}
	
	public interface EnterWork<S>
	{
		StateBuilder doThat(Consumer<S> func);
		
		StateBuilder doNothing();
	}
	
	public interface SwitchTo<S>
	{
		ExitWork<S> switchTo(Class<?> state);
	}
	
	public interface ExitWork<S>
	{
		StateBuilder AndDo(Consumer<S> func);
		
		StateBuilder AndDoNothing();
	}
}