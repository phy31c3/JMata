package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

public interface JMBuilder
{
	class Constructor
	{
		static JMBuilder getNew(boolean isPresent, Consumer<JMMachine> consumer)
		{
			return new JMBuilderImpl(isPresent, consumer);
		}
	}
	
	void ifPresentThenIgnoreThis(Consumer<MachineBuilder> machineBuilder);
	
	void ifPresentThenReplaceToThis(Consumer<MachineBuilder> machineBuilder);
	
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
		
		StateBuilder whenEnter(Consumer<Integer> defaultWork);
		
		StateBuilder whenExit(JMVoidConsumer defaultWork);
		
		StateBuilder whenExit(Consumer<Integer> defaultWork);
		
		<S> WhenEnter<S> whenEnterFrom(Class<S> signal);
		
		JustSwitchTo whenInput(Class<?>... signals);
		
		<S> SwitchTo<S> whenInput(Class<S> signal);
		
		MachineBuilder apply();
		
		public interface WhenEnter<S>
		{
			StateBuilder doThis(Consumer<S> workOnEnter);
			
			StateBuilder doThis(BiConsumer<S, Integer> workOnEnter);
			
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
			StateBuilder AndDo(Consumer<S> workOnExit);
			
			StateBuilder AndDo(BiConsumer<S, Integer> workOnExit);
			
			StateBuilder AndDoNothing();
		}
	}
}