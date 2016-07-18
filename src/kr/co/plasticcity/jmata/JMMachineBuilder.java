package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

public interface JMMachineBuilder
{
	MachineBuilder ifPresentThenIgnoreThis(JMVoidConsumer funcForException);
	
	MachineBuilder ifPresentThenReplaceToThis(JMVoidConsumer funcForException);
	
	public interface MachineBuilder
	{
		StateBuilder defineState(Class<?> state);
		
		GroupBuilder defineGroup(Class<?> group);
		
		void build();
		
		void build(int numMachines);
		
		void buildAndRun();
		
		void buildAndRun(int numMachines);
	}
	
	public interface StateBuilder
	{
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenEnter(Consumer<Integer> defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(Consumer<Integer> defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		WhenEnter<?> whenEnterFrom(Class<?>... signals);
		
		<S> SwitchTo<S, StateBuilder> whenInput(Class<S> signal);
		
		SwitchTo<?, StateBuilder> whenInput(Class<?>... signals);
		
		MachineBuilder apply();
	}
	
	public interface GroupBuilder
	{
		GroupBuilder putStates(Class<?>... states);
		
		<S> SwitchTo<S, GroupBuilder> whenInput(Class<S> signal);
		
		MachineBuilder apply();
	}
	
	public interface WhenEnter<S>
	{
		StateBuilder doThis(Consumer<S> workOnEnter);
		
		StateBuilder doThis(BiConsumer<S, Integer> workOnEnter);
		
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
		
		T AndDo(BiConsumer<S, Integer> workOnExit);
		
		T AndDoNothing();
	}
}