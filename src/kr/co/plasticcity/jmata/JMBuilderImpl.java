package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.function.*;

class JMBuilderImpl implements JMBuilder
{
	private boolean present;
	private Consumer<JMMachine> consumer;
	
	JMBuilderImpl(boolean isPresent, Consumer<JMMachine> consumer)
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
		private Map<Class<?>, JMStateCreater> stateMap;
		
		private MachineBuilderImpl()
		{
			this.stateMap = new HashMap<>();
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
			consumer.accept(JMMachine.getNew(1, stateMap));
		}
		
		@Override
		public void build(int numMachines)
		{
			consumer.accept(JMMachine.getNew(numMachines, stateMap));
		}
		
		@Override
		public void buildAndRun()
		{
			build();
			// TODO
		}
		
		@Override
		public void buildAndRun(int numMachines)
		{
			build(numMachines);
			// TODO
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
				private Class<?>[] signals;
				
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
				private Class<S> signal;
				
				private SwitchToImpl(Class<S> signal)
				{
					this.signal = signal;
				}
				
				@Override
				public WhenExit<S> switchTo(Class<?> stateTag)
				{
					creater.putSwitchRule(signal, stateTag);
					return new WhenExitImpl<S>(signal);
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