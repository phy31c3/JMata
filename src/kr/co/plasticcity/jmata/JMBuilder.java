package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

public interface JMBuilder
{
	class Constructor
	{
		static JMBuilder getNew(Object machineTag, boolean isPresent, JMConsumer<JMMachine> registrator)
		{
			return new JMBuilderImpl(machineTag, isPresent, registrator);
		}
	}
	
	void ifPresentThenIgnoreThis(JMConsumer<StartStateDefiner> definer);
	
	void ifPresentThenReplaceWithThis(JMConsumer<StartStateDefiner> definer);
	
	interface StartStateDefiner
	{
		StateBuilder defineStartState(Class<?> stateTag);
	}
	
	interface MachineBuilder
	{
		StateBuilder defineState(Class<?> stateTag);
		
		MachineBuilder defineTerminateWork(JMVoidConsumer work);
		
		void build();
		
		void buildAndRun();
	}
	
	interface StateBuilder
	{
		/* ================================== enter & exit ================================== */
		StateBuilder whenEnter(JMVoidConsumer defaultWork);
		
		StateBuilder whenEnter(JMSupplier<Object> defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		<S> WhenEnter<S> whenEnterBy(Class<S> signal);
		
		<S extends Enum<S>> WhenEnter<S> whenEnterBy(Enum<S> signal);
		
		WhenEnter<String> whenEnterBy(String signal);
		
		/* ===================================== input ===================================== */
		<S> WhenInput<S> whenInput(Class<S> signal);
		
		SwitchTo whenInput(Class<?>... signals);
		
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(S... signals);
		
		WhenInputPrimitive<String> whenInput(String signal);
		
		WhenInputPrimitive<String> whenInput(String... signals);
		
		/* ====================================== etc ====================================== */
		MachineBuilder apply();
		
		interface WhenEnter<S>
		{
			StateBuilder doThis(JMConsumer<S> workOnEnter);
			
			StateBuilder doThis(JMFunction<S, Object> workOnEnter);
			
			StateBuilder doNothing();
		}
		
		interface WhenInput<S> extends SwitchTo
		{
			SwitchTo doThis(JMConsumer<S> workOnExit);
			
			SwitchTo doNothing();
		}
		
		interface WhenInputPrimitive<S> extends SwitchTo
		{
			SwitchTo doThis(JMVoidConsumer workOnExit);
			
			SwitchTo doThis(JMConsumer<S> workOnExit);
			
			SwitchTo doNothing();
		}
		
		interface SwitchTo
		{
			StateBuilder switchToSelf();
			
			StateBuilder switchTo(Class<?> stateTag);
		}
	}
}