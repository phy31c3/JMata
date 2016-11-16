package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.*;

public interface JMBuilder
{
	class Constructor
	{
		static JMBuilder getNew(Object machineTag, boolean isPresent, JMConsumer<JMMachine> consumer)
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
		/* ================================== enter & exit ================================== */
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenEnter(JMConsumer<Integer> defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(JMConsumer<Integer> defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		<S extends Enum<S>> WhenEnter<S> whenEnterFrom(Enum<S> signal);
		
		WhenEnter<String> whenEnterFrom(String signal);
		
		/* ===================================== input ===================================== */
		SwitchTo whenInput(Class<?>... signals);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenInput<S> whenInput(S... signals);
		
		WhenInput<String> whenInput(String... signals);
		
		<S> WhenInput<S> whenInput(Class<S> signal);
		
		<S extends Enum<S>> WhenInput<S> whenInput(Enum<S> signal);
		
		WhenInput<String> whenInput(String signal);
		
		/* ====================================== etc ====================================== */
		MachineBuilder apply();
		
		public interface WhenEnter<S>
		{
			StateBuilder doThis(JMConsumer<S> workOnEnter);
			
			StateBuilder doThis(JMBiConsumer<S, Integer> workOnEnter);
			
			StateBuilder doNothing();
		}
		
		public interface SwitchTo
		{
			StateBuilder switchToSelf();
			
			StateBuilder switchTo(Class<?> stateTag);
		}
		
		public interface WhenInput<S> extends SwitchTo
		{
			SwitchTo doThis(JMConsumer<S> workOnExit);
			
			SwitchTo doThis(JMBiConsumer<S, Integer> workOnExit);
			
			SwitchTo doNothing();
		}
	}
}