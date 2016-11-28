package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMBuilderImpl implements JMBuilder
{
	private Object machineTag;
	private boolean present;
	private JMConsumer<JMMachine> registrator;
	
	JMBuilderImpl(Object machineTag, boolean isPresent, JMConsumer<JMMachine> registrator)
	{
		this.machineTag = machineTag;
		this.present = isPresent;
		this.registrator = registrator;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(JMConsumer<StartStateDefiner> definer)
	{
		if (present)
		{
			JMLog.debug("[%s] machine already exists, ignoring build", machineTag);
			return;
		}
		else
		{
			definer.accept(new MachineBuilderImpl());
		}
	}
	
	@Override
	public void ifPresentThenReplaceWithThis(JMConsumer<StartStateDefiner> definer)
	{
		if (present)
		{
			JMLog.debug("[%s] machine already exists and will be replaced with a new machine", machineTag);
		}
		definer.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder, StartStateDefiner
	{
		private Class<?> startState;
		private Map<Class<?>, JMStateCreater> stateMap;
		private JMVoidConsumer terminateWork;
		
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
				JMLog.error("[%s] machine : Definition of state [%s] redundancy", machineTag, stateTag.getSimpleName());
			}
			return new StateBuilderImpl(stateTag);
		}
		
		@Override
		public MachineBuilder defineTerminateWork(JMVoidConsumer work)
		{
			terminateWork = work;
			return this;
		}
		
		@Override
		public void build()
		{
			registrator.accept(JMMachine.Constructor.getNew(machineTag, startState, stateMap, terminateWork));
		}
		
		@Override
		public void buildAndRun()
		{
			JMMachine machine = JMMachine.Constructor.getNew(machineTag, startState, stateMap, terminateWork);
			registrator.accept(machine);
			machine.run();
		}
		
		private class StateBuilderImpl implements StateBuilder
		{
			private Class<?> stateTag;
			private JMStateCreater stateCreater;
			
			private StateBuilderImpl(Class<?> stateTag)
			{
				this.stateTag = stateTag;
				this.stateCreater = JMStateCreater.Constructor.getNew(machineTag, stateTag);
			}
			
			@Override
			public StateBuilder whenEnter(JMVoidConsumer defaultWork)
			{
				stateCreater.putEnterFunction(() ->
				{
					defaultWork.accept();
					return null;
				});
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(JMSupplier<Object> defaultWork)
			{
				stateCreater.putEnterFunction(defaultWork);
				return null;
			}
			
			@Override
			public StateBuilder whenExit(JMVoidConsumer defaultWork)
			{
				stateCreater.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public <S> WhenEnter<S> whenEnterFrom(Class<S> signal)
			{
				return new WhenEnterImpl<S>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnter<S> whenEnterFrom(Enum<S> signal)
			{
				return new WhenEnterImpl<S>(signal);
			}
			
			@Override
			public WhenEnter<String> whenEnterFrom(String signal)
			{
				return new WhenEnterImpl<String>(signal);
			}
			
			@Override
			public SwitchTo whenInput(Class<?>... signals)
			{
				return new SwitchToImpl(signals);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public <S extends Enum<S>> WhenInput<S> whenInput(S... signals)
			{
				return new WhenInputImpl<S>(signals);
			}
			
			@Override
			public WhenInput<String> whenInput(String... signals)
			{
				return new WhenInputImpl<String>(signals);
			}
			
			@Override
			public <S> WhenInput<S> whenInput(Class<S> signal)
			{
				return new WhenInputImpl<S>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenInput<S> whenInput(Enum<S> signal)
			{
				return new WhenInputImpl<S>(signal);
			}
			
			@Override
			public WhenInput<String> whenInput(String signal)
			{
				return new WhenInputImpl<String>(signal);
			}
			
			@Override
			public MachineBuilder apply()
			{
				stateMap.put(stateTag, stateCreater);
				return MachineBuilderImpl.this;
			}
			
			private class WhenEnterImpl<S> implements WhenEnter<S>
			{
				private Class<S> signalC;
				private Enum<?> signalE;
				private String signalS;
				
				private WhenEnterImpl(Class<S> signal)
				{
					this.signalC = signal;
				}
				
				private WhenEnterImpl(Enum<?> signal)
				{
					this.signalE = signal;
				}
				
				private WhenEnterImpl(String signal)
				{
					this.signalS = signal;
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(JMConsumer<S> workOnEnter)
				{
					if (signalC != null)
					{
						stateCreater.putEnterFunction(signalC, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					else
					{
						stateCreater.putEnterFunction(signalS, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(JMFunction<S, Object> workOnEnter)
				{
					if (signalC != null)
					{
						stateCreater.putEnterFunction(signalC, (JMFunction<Object, Object>)workOnEnter);
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, (JMFunction<Enum<?>, Object>)workOnEnter);
					}
					else
					{
						stateCreater.putEnterFunction(signalS, (JMFunction<String, Object>)workOnEnter);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doNothing()
				{
					if (signalC != null)
					{
						stateCreater.putEnterFunction(signalC, s -> null);
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, s -> null);
					}
					else
					{
						stateCreater.putEnterFunction(signalS, s -> null);
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class SwitchToImpl implements SwitchTo
			{
				protected Class<?>[] signalsC;
				protected Enum<?>[] signalsE;
				protected String[] signalsS;
				
				protected SwitchToImpl(Class<?>... signals)
				{
					this.signalsC = signals;
				}
				
				protected SwitchToImpl(Enum<?>... signals)
				{
					this.signalsE = signals;
				}
				
				protected SwitchToImpl(String... signals)
				{
					this.signalsS = signals;
				}
				
				@Override
				public StateBuilder switchToSelf()
				{
					return switchTo(stateTag);
				}
				
				@Override
				public StateBuilder switchTo(Class<?> stateTag)
				{
					if (signalsC != null)
					{
						for (Class<?> signal : signalsC)
						{
							stateCreater.putSwitchRule(signal, stateTag);
						}
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putSwitchRule(signal, stateTag);
						}
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putSwitchRule(signal, stateTag);
						}
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class WhenInputImpl<S> extends SwitchToImpl implements WhenInput<S>
			{
				private Class<S> signalC;
				
				private WhenInputImpl(Class<S> signal)
				{
					super(signal);
					this.signalC = signal;
				}
				
				private WhenInputImpl(Enum<?>... signals)
				{
					super(signals);
				}
				
				private WhenInputImpl(String... signals)
				{
					super(signals);
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchTo doThis(JMConsumer<S> workOnExit)
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, (JMConsumer<Object>)workOnExit);
						return new SwitchToImpl(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putExitFunction(signal, (JMConsumer<Enum<?>>)workOnExit);
						}
						return new SwitchToImpl(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putExitFunction(signal, (JMConsumer<String>)workOnExit);
						}
						return new SwitchToImpl(signalsS);
					}
				}
				
				@Override
				public SwitchTo doNothing()
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, s ->
						{
							/* do nothing */
						});
						return new SwitchToImpl(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putExitFunction(signal, s ->
							{
								/* do nothing */
							});
						}
						return new SwitchToImpl(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putExitFunction(signal, s ->
							{
								/* do nothing */
							});
						}
						return new SwitchToImpl(signalsS);
					}
				}
			}
		}
	}
}