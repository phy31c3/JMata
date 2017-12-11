package kr.co.plasticcity.jmata;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface JMBuilder
{
	interface Builder extends JMBuilder
	{
		/* dummy interface */
	}
	
	class Constructor
	{
		static Builder getNew(final Object machineTag, final boolean isPresent, final Consumer<JMMachine> registrator)
		{
			return new JMBuilderImpl(machineTag, isPresent, registrator);
		}
	}
	
	void ifPresentThenIgnoreThis(final Consumer<Definer> definer);
	
	void ifPresentThenReplaceWithThis(final Consumer<Definer> definer);
	
	interface Definer
	{
		StateBuilder defineStartState(final Class stateTag);
	}
	
	interface MachineBuilder
	{
		StateBuilder defineState(final Class stateTag);
		
		MachineBuilder onCreate(final Runnable work);
		
		MachineBuilder onPause(final Runnable work);
		
		MachineBuilder onResume(final Runnable work);
		
		MachineBuilder onStop(final Runnable work);
		
		MachineBuilder onRestart(final Runnable work);
		
		MachineBuilder onTerminate(final Runnable work);
		
		void build();
		
		void buildAndRun();
		
		void buildAndPause();
	}
	
	interface StateBuilder
	{
		/* ================================== enter & exit ================================== */
		StateBuilder whenEnter(final Runnable defaultWork);
		
		StateBuilder whenEnter(final Supplier<Object> defaultWork);
		
		StateBuilder whenExit(final Runnable defaultWork);
		
		<S> WhenEnter<S> whenEnterBy(final Class<S> signal);
		
		<S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final S... signals);
		
		WhenEnterPrimitive<String> whenEnterBy(final String signal);
		
		WhenEnterPrimitive<String> whenEnterBy(final String... signals);
		
		/* ===================================== input ===================================== */
		<S> WhenInput<S> whenInput(final Class<S> signal);
		
		WhenInputClasses whenInput(final Class... signals);
		
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenInputPrimitive<S> whenInput(final S... signals);
		
		WhenInputPrimitive<String> whenInput(final String signal);
		
		WhenInputPrimitive<String> whenInput(final String... signals);
		
		/* ====================================== etc ====================================== */
		MachineBuilder apply();
		
		interface WhenEnter<S>
		{
			StateBuilder doThis(final Consumer<S> workOnEnter);
			
			StateBuilder doThis(final Function<S, Object> workOnEnter);
			
			StateBuilder doNothing();
		}
		
		interface WhenEnterPrimitive<S> extends WhenEnter<S>
		{
			StateBuilder doThis(final Runnable workOnEnter);
			
			StateBuilder doThis(final Supplier<Object> workOnEnter);
		}
		
		interface WhenInput<S> extends SwitchTo
		{
			SwitchOrNot doThis(final Consumer<S> workOnExit);
			
			SwitchTo doNothing();
		}
		
		interface WhenInputClasses extends SwitchTo
		{
			SwitchOrNot doThis(final Runnable workOnExit);
			
			SwitchTo doNothing();
		}
		
		interface WhenInputPrimitive<S> extends WhenInput<S>, WhenInputClasses, SwitchTo
		{
			/* nothing */
		}
		
		interface SwitchTo
		{
			StateBuilder switchTo(final Class stateTag);
			
			StateBuilder switchToSelf();
		}
		
		interface SwitchOrNot extends SwitchTo
		{
			StateBuilder notSwitch();
		}
	}
}