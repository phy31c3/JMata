package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

public interface JMMachineBuilder
{
	StateBuilder defineState(Class<?> state);
	
	GroupBuilder defineGroup(Class<?> group);
	
	Optional commit();
	
	public interface StateBuilder
	{
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		WhenEnter<?> whenEnterFrom(Class<?>... signals);
		
		<S> SwitchTo<S, StateBuilder> whenInput(Class<S> signal);
		
		SwitchTo<?, StateBuilder> whenInput(Class<?>... signals);
		
		JMMachineBuilder apply();
	}
	
	public interface GroupBuilder
	{
		GroupBuilder putStates(Class<?>... states);
		
		<S> SwitchTo<S, GroupBuilder> whenInput(Class<S> signal);
		
		JMMachineBuilder apply();
	}
	
	public interface WhenEnter<S>
	{
		StateBuilder doThis(Consumer<S> workOnEnter);
		
		StateBuilder doNothing();
	}
	
	public interface SwitchTo<S, T>
	{
		StateBuilder justSwitchTo(Class<?> state);
		
		WhenExit<S, T> switchTo(Class<?> state);
	}
	
	public interface WhenExit<S, T>
	{
		T AndDo(Consumer<S> workOnExit);
		
		T AndDoNothing();
	}
	
	public interface Optional
	{
		void ifReplaced(JMVoidConsumer func);
	}
}