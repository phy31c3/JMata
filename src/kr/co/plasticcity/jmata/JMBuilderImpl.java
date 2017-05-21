package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;

import kr.co.plasticcity.jmata.function.JMConsumer;
import kr.co.plasticcity.jmata.function.JMFunction;
import kr.co.plasticcity.jmata.function.JMSupplier;
import kr.co.plasticcity.jmata.function.JMVoidConsumer;

class JMBuilderImpl implements JMBuilder
{
	private final Object machineTag;
	private final boolean present;
	private final JMConsumer<JMMachine> registrator;
	
	JMBuilderImpl(final Object machineTag, final boolean isPresent, final JMConsumer<JMMachine> registrator)
	{
		this.machineTag = machineTag;
		this.present = isPresent;
		this.registrator = registrator;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(final JMConsumer<StartStateDefiner> definer)
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
	public void ifPresentThenReplaceWithThis(final JMConsumer<StartStateDefiner> definer)
	{
		if (present)
		{
			JMLog.debug(JMLog.REPLACE_MACHINE, machineTag);
		}
		definer.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder, StartStateDefiner
	{
		private final Map<Class<?>, JMState> stateMap;
		private Class<?> startState;
		private JMVoidConsumer terminateWork;
		
		private MachineBuilderImpl()
		{
			this.stateMap = new HashMap<>();
		}
		
		@Override
		public StateBuilder defineStartState(final Class<?> stateTag)
		{
			startState = stateTag;
			return defineState(stateTag);
		}
		
		@Override
		public StateBuilder defineState(final Class<?> stateTag)
		{
			if (stateMap.containsKey(stateTag))
			{
				JMLog.error(JMLog.STATE_DEFINITION_DUPLICATED, machineTag, stateTag.getSimpleName());
			}
			return new StateBuilderImpl(stateTag);
		}
		
		@Override
		public MachineBuilder defineTerminateWork(final JMVoidConsumer work)
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
			private final Class<?> stateTag;
			private final JMState state;
			
			private StateBuilderImpl(final Class<?> stateTag)
			{
				this.stateTag = stateTag;
				this.state = JMState.Constructor.getNew(machineTag, stateTag);
			}
			
			@Override
			public StateBuilder whenEnter(final JMVoidConsumer defaultWork)
			{
				state.putEnterFunction(() ->
				{
					defaultWork.accept();
					return null;
				});
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(final JMSupplier<Object> defaultWork)
			{
				state.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(final JMVoidConsumer defaultWork)
			{
				state.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public <S> WhenEnter<S> whenEnterBy(final Class<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final Enum<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnterPrimitive<S> whenEnterBy(final S[] signals)
			{
				return new WhenEnterImpl<>(signals);
			}
			
			@Override
			public WhenEnterPrimitive<String> whenEnterBy(final String signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public WhenEnterPrimitive<String> whenEnterBy(final String... signals)
			{
				return new WhenEnterImpl<>(signals);
			}
			
			@Override
			public <S> WhenInput<S> whenInput(final Class<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public SwitchTo whenInput(final Class<?>... signals)
			{
				return new SwitchToImpl(signals);
			}
			
			@Override
			public <S extends Enum<S>> WhenInputPrimitive<S> whenInput(final Enum<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenInputPrimitive<S> whenInput(final S[] signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public WhenInputPrimitive<String> whenInput(final String signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public WhenInputPrimitive<String> whenInput(final String... signals)
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
				private final Class<S> signalC;
				private final Enum<?>[] signalsE;
				private final String[] signalsS;
				
				private WhenEnterImpl(final Class<S> signal)
				{
					this.signalC = signal;
					this.signalsE = null;
					this.signalsS = null;
				}
				
				private WhenEnterImpl(final Enum<?>... signal)
				{
					this.signalC = null;
					this.signalsE = signal;
					this.signalsS = null;
				}
				
				private WhenEnterImpl(final String... signal)
				{
					this.signalC = null;
					this.signalsE = null;
					this.signalsS = signal;
				}
				
				@Override
				public StateBuilder doThis(final JMVoidConsumer workOnEnter)
				{
					return doThis((S s) -> workOnEnter.accept());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(final JMConsumer<S> workOnEnter)
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, s ->
						{
							workOnEnter.accept((S)s);
							return null;
						});
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putEnterFunction(signal, s ->
							{
								workOnEnter.accept((S)s);
								return null;
							});
						}
					}
					else if (signalsS != null)
					{
						for (String signal : signalsS)
						{
							state.putEnterFunction(signal, s ->
							{
								workOnEnter.accept((S)s);
								return null;
							});
						}
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
				public StateBuilder doThis(final JMFunction<S, Object> workOnEnter)
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, (JMFunction<Object, Object>)workOnEnter);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putEnterFunction(signal, (JMFunction<Enum<?>, Object>)workOnEnter);
						}
					}
					else if (signalsS != null)
					{
						for (String signal : signalsS)
						{
							state.putEnterFunction(signal, (JMFunction<String, Object>)workOnEnter);
						}
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
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							state.putEnterFunction(signal, s -> null);
						}
					}
					else if (signalsS != null)
					{
						for (String signal : signalsS)
						{
							state.putEnterFunction(signal, s -> null);
						}
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class WhenInputImpl<S> extends SwitchToImpl implements WhenInputPrimitive<S>
			{
				private final Class<S> signalC;
				
				private WhenInputImpl(final Class<S> signal)
				{
					super(signal);
					this.signalC = signal;
				}
				
				private WhenInputImpl(final Enum<?>... signals)
				{
					super(signals);
					this.signalC = null;
				}
				
				private WhenInputImpl(final String... signals)
				{
					super(signals);
					this.signalC = null;
				}
				
				@Override
				public SwitchTo doThis(final JMVoidConsumer workOnExit)
				{
					return doThis((S s) -> workOnExit.accept());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchTo doThis(final JMConsumer<S> workOnExit)
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
				final Class<?>[] signalsC;
				final Enum<?>[] signalsE;
				final String[] signalsS;
				
				SwitchToImpl(final Class<?>... signals)
				{
					this.signalsC = signals;
					this.signalsE = null;
					this.signalsS = null;
				}
				
				SwitchToImpl(final Enum<?>... signals)
				{
					this.signalsC = null;
					this.signalsE = signals;
					this.signalsS = null;
				}
				
				SwitchToImpl(final String... signals)
				{
					this.signalsC = null;
					this.signalsE = null;
					this.signalsS = signals;
				}
				
				@Override
				public StateBuilder switchToSelf()
				{
					return switchTo(stateTag);
				}
				
				@Override
				public StateBuilder switchTo(final Class<?> stateTag)
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
					else if (signalsS != null)
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