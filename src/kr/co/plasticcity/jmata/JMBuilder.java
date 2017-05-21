package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

public interface JMBuilder
{
	class Constructor
	{
		static JMBuilder getNew(final Object machineTag, final boolean isPresent, final JMConsumer<JMMachine> registrator)
		{
			return new JMBuilderImpl(machineTag, isPresent, registrator);
		}
	}
	
	void ifPresentThenIgnoreThis(final JMConsumer<StartStateDefiner> definer);
	
	void ifPresentThenReplaceWithThis(final JMConsumer<StartStateDefiner> definer);
	
	interface StartStateDefiner
	{
		StateBuilder defineStartState(final Class<?> stateTag);
	}
	
	interface MachineBuilder
	{
		StateBuilder defineState(final Class<?> stateTag);
		
		MachineBuilder defineTerminateWork(final JMVoidConsumer work);
		
		void build();
		
		void buildAndRun();
	}
	
	interface StateBuilder
	{
		/* ================================== enter & exit ================================== */
		StateBuilder whenEnter(final JMVoidConsumer defaultWork);
		
		StateBuilder whenEnter(final JMSupplier<Object> defaultWork);
		
		StateBuilder whenExit(final JMVoidConsumer defaultWork);
		
		<S> WhenEnter<S> whenEnterBy(final Class<S> signal);
		
		<S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final S... signals);
		
		WhenEnterPrimitive<String> whenEnterBy(final String signal);
		
		WhenEnterPrimitive<String> whenEnterBy(final String... signals);
		
		/* ===================================== input ===================================== */
		<S> WhenInput<S> whenInput(final Class<S> signal);
		
		SwitchTo whenInput(final Class<?>... signals);
		
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(final S... signals);
		
		WhenInputPrimitive<String> whenInput(final String signal);
		
		WhenInputPrimitive<String> whenInput(final String... signals);
		
		/* ====================================== etc ====================================== */
		MachineBuilder apply();
		
		interface WhenEnter<S>
		{
			StateBuilder doThis(final JMConsumer<S> workOnEnter);
			
			StateBuilder doThis(final JMFunction<S, Object> workOnEnter);
			
			StateBuilder doNothing();
		}
		
		interface WhenEnterPrimitive<S> extends WhenEnter<S>
		{
			StateBuilder doThis(final JMVoidConsumer workOnEnter);
			
			StateBuilder doThis(final JMSupplier<Object> workOnEnter);
		}
		
		interface WhenInput<S> extends SwitchTo
		{
			SwitchTo doThis(final JMConsumer<S> workOnExit);
			
			SwitchTo doNothing();
		}
		
		interface WhenInputPrimitive<S> extends WhenInput<S>, SwitchTo
		{
			SwitchTo doThis(final JMVoidConsumer workOnExit);
		}
		
		interface SwitchTo
		{
			StateBuilder switchToSelf();
			
			StateBuilder switchTo(final Class<?> stateTag);
		}
	}
}