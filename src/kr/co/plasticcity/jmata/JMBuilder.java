package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

public interface JMBuilder
{
	class Constructor
	{
		static JMBuilder getNew(Class<?> machineTag, boolean isPresent, JMConsumer<JMMachine> consumer)
		{
			return new JMBuilderImpl(machineTag, isPresent, consumer);
		}
	}
	
	void ifPresentThenIgnoreThis(JMConsumer<StartStateDefiner> definer);
	
	void ifPresentThenReplaceToThis(JMConsumer<StartStateDefiner> definer);
	
	public interface StartStateDefiner
	{
		StateBuilder defineStartState(Class<?> stateTag);
	}
	
	public interface MachineBuilder
	{
		StateBuilder defineState(Class<?> stateTag);
		
		void build();
		
		void build(int numMachines);
		
		void buildAndRun();
		
		void buildAndRun(int numMachines);
	}
	
	public interface StateBuilder
	{
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenEnter(JMConsumer<Integer> defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(JMConsumer<Integer> defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		<S extends Enum<S>> WhenEnter<S> whenEnterFrom(Enum<S> signal);
		
		WhenEnter<String> whenEnterFrom(String signal);
		
		WhenEnter<Integer> whenEnterFrom(Integer signal);
		
		JustSwitchTo whenInput(Class<?>... signals);
		
		JustSwitchTo whenInput(Enum<?>... signals);
		
		JustSwitchTo whenInput(String... signals);
		
		JustSwitchTo whenInput(Integer... signals);
		
		<S> SwitchTo<S> whenInput(Class<S> signal);
		
		<S extends Enum<S>> SwitchTo<Enum<S>> whenInput(Enum<S> signal);
		
		SwitchTo<String> whenInput(String signal);
		
		SwitchTo<Integer> whenInput(Integer signal);
		
		MachineBuilder apply();
		
		public interface WhenEnter<S>
		{
			StateBuilder doThis(JMConsumer<S> workOnEnter);
			
			StateBuilder doThis(JMBiConsumer<S, Integer> workOnEnter);
			
			StateBuilder doNothing();
		}
		
		public interface JustSwitchTo
		{
			StateBuilder justSwitchTo(Class<?> stateTag);
		}
		
		public interface SwitchTo<S> extends JustSwitchTo
		{
			WhenExit<S> switchTo(Class<?> stateTag);
		}
		
		public interface WhenExit<S>
		{
			StateBuilder AndDo(JMConsumer<S> workOnExit);
			
			StateBuilder AndDo(JMBiConsumer<S, Integer> workOnExit);
			
			StateBuilder AndDoNothing();
		}
	}
}