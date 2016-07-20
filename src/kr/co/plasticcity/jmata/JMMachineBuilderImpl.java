package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import javax.swing.text.html.HTML.*;

import kr.co.plasticcity.jmata.JMMachineBuilder.*;
import kr.co.plasticcity.jmata.function.*;

class JMMachineBuilderImpl<M> implements JMMachineBuilder
{
	private boolean present;
	private Consumer<JMMachine<M>> consumer;
	
	JMMachineBuilderImpl(boolean isPresent, Consumer<JMMachine<M>> consumer)
	{
		this.present = isPresent;
		this.consumer = consumer;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(Consumer<MachineBuilder> machineBuilder)
	{
		if (present)
		{
			return;
		}
		else
		{
			machineBuilder.accept(new MachineBuilderImpl());
		}
	}
	
	@Override
	public void ifPresentThenReplaceToThis(Consumer<MachineBuilder> machineBuilder)
	{
		machineBuilder.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder
	{
		private Map<Class<?>, JMVoidConsumer> defaultEnter;
		private Map<Class<?>, Consumer<Integer>> defaultEnterIdx;
		private Map<Class<?>, Map<Class<?>, Consumer<?>>> signalEnter;
		private Map<Class<?>, Map<Class<?>, BiConsumer<?, Integer>>> signalEnterBi;
		
		private Map<Class<?>, JMVoidConsumer> defaultExit;
		private Map<Class<?>, Consumer<Integer>> defaultExitIdx;
		private Map<Class<?>, Map<Class<?>, Consumer<?>>> signalExit;
		private Map<Class<?>, Map<Class<?>, BiConsumer<?, Integer>>> signalExitBi;
		
		private Map<Class<?>, Map<Class<?>, Class<?>>> switchMap;
		
		@Override
		public StateBuilder defineState(Class<?> state)
		{
			return new StateBuilderImpl<>(state);
		}
		
		@Override
		public GroupBuilder defineGroup(Class<?> group)
		{
			return null;
		}
		
		@Override
		public void build()
		{
		}
		
		@Override
		public void build(int numMachines)
		{
		}
		
		@Override
		public void buildAndRun()
		{
		}
		
		@Override
		public void buildAndRun(int numMachines)
		{
		}
		
		private class StateBuilderImpl<T> implements StateBuilder
		{
			private Class<T> stateTag;
			
			private StateBuilderImpl(Class<T> stateTag)
			{
				this.stateTag = stateTag;
			}
			
			@Override
			public StateBuilder whenEnter(JMVoidConsumer defaultWork)
			{
				defaultEnter.put(stateTag, defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(Consumer<Integer> defaultWork)
			{
				
				return null;
			}
			
			@Override
			public StateBuilder whenExit(JMVoidConsumer defaultWork)
			{
				return null;
			}
			
			@Override
			public StateBuilder whenExit(Consumer<Integer> defaultWork)
			{
				return null;
			}
			
			@Override
			public <S> WhenEnter<S> whenEnterFrom(Class<S> signal)
			{
				return null;
			}
			
			@Override
			public WhenEnter<?> whenEnterFrom(Class<?>... signals)
			{
				return null;
			}
			
			@Override
			public <S> SwitchTo<S, StateBuilder> whenInput(Class<S> signal)
			{
				return null;
			}
			
			@Override
			public SwitchTo<?, StateBuilder> whenInput(Class<?>... signals)
			{
				return null;
			}
			
			@Override
			public MachineBuilder apply()
			{
				return null;
			}
		}
		
		private class GroupBuilderImpl<G> implements GroupBuilder
		{
			private Class<G> groupTag;
			
			private GroupBuilderImpl(Class<G> groupTag)
			{
				this.groupTag = groupTag;
			}
			
			@Override
			public GroupBuilder putStates(Class<?>... states)
			{
				return null;
			}
			
			@Override
			public <S> SwitchTo<S, GroupBuilder> whenInput(Class<S> signal)
			{
				return null;
			}
			
			@Override
			public MachineBuilder apply()
			{
				return null;
			}
		}
		
		private class WhenEnterImpl<T, S> implements WhenEnter<S>
		{
			private Class<T> stateTag;
			
			private WhenEnterImpl(Class<T> stateTag)
			{
				this.stateTag = stateTag;
			}
			
			@Override
			public StateBuilder doThis(Consumer<S> workOnEnter)
			{
				return null;
			}
			
			@Override
			public StateBuilder doThis(BiConsumer<S, Integer> workOnEnter)
			{
				return null;
			}
			
			@Override
			public StateBuilder doNothing()
			{
				return null;
			}
		}
		
		private class SwitchToImpl<S, T> implements SwitchTo<S, T>
		{
			@Override
			public StateBuilder justSwitchTo(Class<?> state)
			{
				return null;
			}
			
			@Override
			public WhenExit<S, T> switchTo(Class<?> state)
			{
				return null;
			}
		}
		
		private class WhenExitImpl<S, T> implements WhenExit<S, T>
		{
			@Override
			public T AndDo(Consumer<S> workOnExit)
			{
				return null;
			}
			
			@Override
			public T AndDo(BiConsumer<S, Integer> workOnExit)
			{
				return null;
			}
			
			@Override
			public T AndDoNothing()
			{
				return null;
			}
		}
	}
}