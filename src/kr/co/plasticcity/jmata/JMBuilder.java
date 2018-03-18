package kr.co.plasticcity.jmata;

import kr.co.plasticcity.jmata.function.Consumer;
import kr.co.plasticcity.jmata.function.Function;
import kr.co.plasticcity.jmata.function.Supplier;

public interface JMBuilder
{
	interface Builder extends JMBuilder
	{
		/* dummy interface */
	}
	
	AndDo ifPresentThenIgnoreThis(final Consumer<BaseDefiner> definer);
	
	AndDo ifPresentThenReplaceWithThis(final Consumer<BaseDefiner> definer);
	
	interface AndDo
	{
		void andDo(final Runnable work);
	}
	
	interface BaseDefiner extends StartDefiner
	{
		StateBuilder<StartDefiner> defineBaseRule();
	}
	
	interface StartDefiner
	{
		StateBuilder<MachineBuilder> defineStartState(final Class stateTag);
	}
	
	interface MachineBuilder
	{
		StateBuilder<MachineBuilder> defineState(final Class stateTag);
		
		MachineBuilder onCreate(final Runnable work);
		
		MachineBuilder onPause(final Runnable work);
		
		MachineBuilder onResume(final Runnable work);
		
		MachineBuilder onStop(final Runnable work);
		
		MachineBuilder onRestart(final Runnable work);
		
		MachineBuilder onTerminate(final Runnable work);
		
		/**
		 * default is true
		 */
		MachineBuilder setLogEnabled(final boolean enabled);
		
		void build();
		
		void buildAndRun();
		
		void buildAndPause();
	}
	
	interface StateBuilder<R>
	{
		/* ================================== enter & exit ================================== */
		StateBuilder<R> whenEnter(final Runnable defaultWork);
		
		StateBuilder<R> whenEnter(final Supplier<Object> defaultWork);
		
		StateBuilder<R> whenExit(final Runnable defaultWork);
		
		<S> WhenEnter<S, R> whenEnterBy(final Class<S> signal);
		
		<S extends Enum<S>> WhenEnterPrimitive<S, R> whenEnterBy(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenEnterPrimitive<S, R> whenEnterBy(final S... signals);
		
		WhenEnterPrimitive<String, R> whenEnterBy(final String signal);
		
		WhenEnterPrimitive<String, R> whenEnterBy(final String... signals);
		
		/* ===================================== input ===================================== */
		<S> WhenInput<S, R> whenInput(final Class<S> signal);
		
		WhenInputClasses<R> whenInput(final Class... signals);
		
		<S extends Enum<S>> WhenInputPrimitive<S, R> whenInput(final Enum<S> signal);
		
		@SuppressWarnings("unchecked")
		<S extends Enum<S>> WhenInputPrimitive<S, R> whenInput(final S... signals);
		
		WhenInputPrimitive<String, R> whenInput(final String signal);
		
		WhenInputPrimitive<String, R> whenInput(final String... signals);
		
		/* ====================================== etc ====================================== */
		R apply();
		
		interface WhenEnter<S, R>
		{
			StateBuilder<R> doThis(final Consumer<S> workOnEnter);
			
			StateBuilder<R> doThis(final Function<S, Object> workOnEnter);
			
			StateBuilder<R> doNothing();
		}
		
		interface WhenEnterPrimitive<S, R> extends WhenEnter<S, R>
		{
			StateBuilder<R> doThis(final Runnable workOnEnter);
			
			StateBuilder<R> doThis(final Supplier<Object> workOnEnter);
		}
		
		interface WhenInput<S, R> extends SwitchOrNot<R>
		{
			SwitchOrNot<R> doThis(final Consumer<S> workOnExit);
			
			SwitchTo<R> doNothing();
		}
		
		interface WhenInputClasses<R> extends SwitchOrNot<R>
		{
			SwitchOrNot<R> doThis(final Runnable workOnExit);
			
			SwitchTo<R> doNothing();
		}
		
		interface WhenInputPrimitive<S, R> extends WhenInput<S, R>, WhenInputClasses<R>
		{
			/* nothing */
		}
		
		interface SwitchTo<R>
		{
			StateBuilder<R> switchTo(final Class stateTag);
			
			StateBuilder<R> switchToSelf();
		}
		
		interface SwitchOrNot<R> extends SwitchTo<R>
		{
			StateBuilder<R> dontSwitch();
		}
	}
}