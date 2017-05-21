package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

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
			JMLog.debug(JMLog.IGNORE_MACHINE_BUILD, machineTag);
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
			JMLog.debug(JMLog.REPLACE_MACHINE, machineTag);
		}
		definer.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder, StartStateDefiner
	{
		private Class<?> startState;
		private Map<Class<?>, JMState> stateMap;
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
				JMLog.error(JMLog.STATE_DEFINITION_DUPLICATED, machineTag, stateTag.getSimpleName());
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
			private JMState state;
			
			private StateBuilderImpl(Class<?> stateTag)
			{
				this.stateTag = stateTag;
				this.state = JMState.Constructor.getNew(machineTag, stateTag);
			}
			
			@Override
			public StateBuilder whenEnter(JMVoidConsumer defaultWork)
			{
				state.putEnterFunction(() ->
				{
					defaultWork.accept();
					return null;
				});
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(JMSupplier<Object> defaultWork)
			{
				state.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(JMVoidConsumer defaultWork)
			{
				state.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public <S> WhenEnter<S> whenEnterBy(Class<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(Enum<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public WhenEnterPrimitive<String> whenEnterBy(String signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S> WhenInput<S> whenInput(Class<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public SwitchTo whenInput(Class<?>... signals)
			{
				return new SwitchToImpl(signals);
			}
			
			@Override
			public <S extends Enum<S>> WhenInputPrimitive<S> whenInput(Enum<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public <S extends Enum<S>> WhenInputPrimitive<S> whenInput(S... signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public WhenInputPrimitive<String> whenInput(String signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public WhenInputPrimitive<String> whenInput(String... signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public MachineBuilder apply()
			{
				stateMap.put(stateTag, state);
				return MachineBuilderImpl.this;
			}
			
			private class WhenEnterImpl<S> implements WhenEnterPrimitive<S>
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
				public StateBuilder doThis(final JMVoidConsumer workOnEnter)
				{
					return doThis((S s) -> workOnEnter.accept());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(JMConsumer<S> workOnEnter)
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					else if (signalE != null)
					{
						state.putEnterFunction(signalE, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					else
					{
						state.putEnterFunction(signalS, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doThis(final JMSupplier<Object> workOnEnter)
				{
					return doThis((S s) -> workOnEnter.get());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(JMFunction<S, Object> workOnEnter)
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, (JMFunction<Object, Object>)workOnEnter);
					}
					else if (signalE != null)
					{
						state.putEnterFunction(signalE, (JMFunction<Enum<?>, Object>)workOnEnter);
					}
					else
					{
						state.putEnterFunction(signalS, (JMFunction<String, Object>)workOnEnter);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doNothing()
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, s -> null);
					}
					else if (signalE != null)
					{
						state.putEnterFunction(signalE, s -> null);
					}
					else
					{
						state.putEnterFunction(signalS, s -> null);
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class WhenInputImpl<S> extends SwitchToImpl implements WhenInputPrimitive<S>
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
				public SwitchTo doThis(final JMVoidConsumer workOnExit)
				{
					return doThis((S s) -> workOnExit.accept());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchTo doThis(JMConsumer<S> workOnExit)
				{
					if (signalC != null)
					{
						state.putExitFunction(signalC, (JMConsumer<Object>)workOnExit);
						return new SwitchToImpl(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putExitFunction(signal, (JMConsumer<Enum<?>>)workOnExit);
						}
						return new SwitchToImpl(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							state.putExitFunction(signal, (JMConsumer<String>)workOnExit);
						}
						return new SwitchToImpl(signalsS);
					}
				}
				
				@Override
				public SwitchTo doNothing()
				{
					if (signalC != null)
					{
						state.putExitFunction(signalC, s ->
						{
							/* do nothing */
						});
						return new SwitchToImpl(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putExitFunction(signal, s ->
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
							state.putExitFunction(signal, s ->
							{
								/* do nothing */
							});
						}
						return new SwitchToImpl(signalsS);
					}
				}
			}
			
			private class SwitchToImpl implements SwitchTo
			{
				Class<?>[] signalsC;
				Enum<?>[] signalsE;
				String[] signalsS;
				
				SwitchToImpl(Class<?>... signals)
				{
					this.signalsC = signals;
				}
				
				SwitchToImpl(Enum<?>... signals)
				{
					this.signalsE = signals;
				}
				
				SwitchToImpl(String... signals)
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
							state.putSwitchRule(signal, stateTag);
						}
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putSwitchRule(signal, stateTag);
						}
					}
					else
					{
						for (String signal : signalsS)
						{
							state.putSwitchRule(signal, stateTag);
						}
					}
					return StateBuilderImpl.this;
				}
			}
		}
	}
}