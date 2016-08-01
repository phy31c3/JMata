package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

class JMBuilderImpl implements JMBuilder
{
	private Class<?> machineTag;
	private boolean present;
	private Consumer<JMMachine> consumer;
	
	JMBuilderImpl(Class<?> machineTag, boolean isPresent, Consumer<JMMachine> consumer)
	{
		this.machineTag = machineTag;
		this.present = isPresent;
		this.consumer = consumer;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(Consumer<StartStateDefiner> machineBuilder)
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
	public void ifPresentThenReplaceToThis(Consumer<StartStateDefiner> machineBuilder)
	{
		machineBuilder.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder, StartStateDefiner
	{
		private Class<?> startState;
		private Map<Class<?>, JMStateCreater> stateMap;
		
		private MachineBuilderImpl()
		{
			this.stateMap = new HashMap<>();
		}
		
		@Override
		public StateBuilder defineStartState(Class<?> stateTag)
		{
			startState = stateTag;
			return defineState(stateTag);
		}
		
		@Override
		public StateBuilder defineState(Class<?> stateTag)
		{
			if (stateMap.containsKey(stateTag))
			{
				JMLog.out("State '%s' 중복 정의", stateTag.getSimpleName());
			}
			return new StateBuilderImpl(stateTag);
		}
		
		@Override
		public void build()
		{
			consumer.accept(JMMachine.getNew(machineTag, 1, startState, stateMap));
		}
		
		@Override
		public void build(int numMachines)
		{
			consumer.accept(JMMachine.getNew(machineTag, numMachines, startState, stateMap));
		}
		
		@Override
		public void buildAndRun()
		{
			JMMachine machine = JMMachine.getNew(machineTag, 1, startState, stateMap);
			consumer.accept(machine);
			machine.runAll();
		}
		
		@Override
		public void buildAndRun(int numMachines)
		{
			JMMachine machine = JMMachine.getNew(machineTag, numMachines, startState, stateMap);
			consumer.accept(machine);
			machine.runAll();
		}
		
		private class StateBuilderImpl implements StateBuilder
		{
			private Class<?> stateTag;
			private JMStateCreater creater;
			
			private StateBuilderImpl(Class<?> tag)
			{
				this.stateTag = tag;
				this.creater = JMStateCreater.getNew(tag);
			}
			
			@Override
			public StateBuilder whenEnter(JMVoidConsumer defaultWork)
			{
				creater.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(Consumer<Integer> defaultWork)
			{
				creater.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(JMVoidConsumer defaultWork)
			{
				creater.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(Consumer<Integer> defaultWork)
			{
				creater.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public <S> WhenEnter<S> whenEnterFrom(Class<S> signal)
			{
				return new WhenEnterImpl<S>(signal);
			}
			
			@Override
			public JustSwitchTo whenInput(Class<?>... signals)
			{
				return new JustSwitchToImpl(signals);
			}
			
			@Override
			public <S> SwitchTo<S> whenInput(Class<S> signal)
			{
				return new SwitchToImpl<S>(signal);
			}
			
			@Override
			public MachineBuilder apply()
			{
				stateMap.put(stateTag, creater);
				return MachineBuilderImpl.this;
			}
			
			private class WhenEnterImpl<S> implements WhenEnter<S>
			{
				private Class<S> signal;
				
				private WhenEnterImpl(Class<S> signal)
				{
					this.signal = signal;
				}
				
				@Override
				public StateBuilder doThis(Consumer<S> workOnEnter)
				{
					creater.putEnterFunction(signal, workOnEnter);
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doThis(BiConsumer<S, Integer> workOnEnter)
				{
					creater.putEnterFunction(signal, workOnEnter);
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doNothing()
				{
					creater.putEnterFunction(signal, p ->
					{
						/* do nothing */
					});
					return StateBuilderImpl.this;
				}
			}
			
			private class JustSwitchToImpl implements JustSwitchTo
			{
				protected Class<?>[] signals;
				
				private JustSwitchToImpl(Class<?>... signals)
				{
					this.signals = signals;
				}
				
				@Override
				public StateBuilder justSwitchTo(Class<?> stateTag)
				{
					for (Class<?> signal : signals)
					{
						creater.putSwitchRule(signal, stateTag);
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class SwitchToImpl<S> extends JustSwitchToImpl implements SwitchTo<S>
			{
				private SwitchToImpl(Class<S> signal)
				{
					this.signals = new Class<?>[] { signal };
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public WhenExit<S> switchTo(Class<?> stateTag)
				{
					creater.putSwitchRule(signals[0], stateTag);
					return new WhenExitImpl<S>((Class<S>)signals[0]);
				}
			}
			
			private class WhenExitImpl<S> implements WhenExit<S>
			{
				private Class<S> signal;
				
				private WhenExitImpl(Class<S> signal)
				{
					this.signal = signal;
				}
				
				@Override
				public StateBuilder AndDo(Consumer<S> workOnExit)
				{
					creater.putExitFunction(signal, workOnExit);
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder AndDo(BiConsumer<S, Integer> workOnExit)
				{
					creater.putExitFunction(signal, workOnExit);
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder AndDoNothing()
				{
					creater.putExitFunction(signal, p ->
					{
						/* do nothing */
					});
					return StateBuilderImpl.this;
				}
			}
		}
	}
}