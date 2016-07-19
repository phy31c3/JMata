package kr.co.plasticcity.jmata;

import java.util.function.*;

import kr.co.plasticcity.jmata.JMMachineBuilder.*;
import kr.co.plasticcity.jmata.function.*;

class JMMachineBuilderImpl implements JMMachineBuilder
{
	JMMachineBuilderImpl()
	{
		
	}
	
	@Override
	public void ifPresentThenIgnoreThis(Consumer<MachineBuilder> machineBuilder)
	{
	}
	
	@Override
	public void ifPresentThenReplaceToThis(Consumer<MachineBuilder> machineBuilder)
	{
	}
	
	class MachineBuilderImpl implements MachineBuilder
	{
		@Override
		public StateBuilder defineState(Class<?> state)
		{
			return null;
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
	}
	
	class StateBuilderImpl implements StateBuilder
	{
		@Override
		public StateBuilder whenEnter(JMVoidConsumer defaultWork)
		{
			return null;
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
	
	class GroupBuilderImpl implements GroupBuilder
	{
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
	
	class WhenEnterImpl<S> implements WhenEnter<S>
	{
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
	
	class SwitchToImpl<S, T> implements SwitchTo<S, T>
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
	
	class WhenExitImpl<S, T> implements WhenExit<S, T>
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