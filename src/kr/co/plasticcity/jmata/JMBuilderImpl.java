package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class JMBuilderImpl implements JMBuilder.Builder
{
	private final Object machineTag;
	private final boolean present;
	private final Consumer<JMMachine> registrator;
	
	JMBuilderImpl(final Object machineTag, final boolean isPresent, final Consumer<JMMachine> registrator)
	{
		this.machineTag = machineTag;
		this.present = isPresent;
		this.registrator = registrator;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(final Consumer<Definer> definer)
	{
		if (present)
		{
			JMLog.debug(out -> out.print(JMLog.IGNORE_MACHINE_BUILD, JMLog.getPackagelessName(machineTag)));
		}
		else
		{
			definer.accept(new MachineBuilderImpl());
		}
	}
	
	@Override
	public void ifPresentThenReplaceWithThis(final Consumer<Definer> definer)
	{
		if (present)
		{
			JMLog.debug(out -> out.print(JMLog.REPLACE_MACHINE, JMLog.getPackagelessName(machineTag)));
		}
		definer.accept(new MachineBuilderImpl());
	}
	
	private class MachineBuilderImpl implements MachineBuilder, Definer
	{
		private final Map<Class, JMState> stateMap;
		private Class startState;
		private Runnable onCreate;
		private Runnable onPause;
		private Runnable onResume;
		private Runnable onStop;
		private Runnable onRestart;
		private Runnable onTerminate;
		
		private MachineBuilderImpl()
		{
			this.stateMap = new HashMap<>();
		}
		
		@Override
		public StateBuilder defineStartState(final Class stateTag)
		{
			startState = stateTag;
			return defineState(stateTag);
		}
		
		@Override
		public StateBuilder defineState(final Class stateTag)
		{
			if (stateMap.containsKey(stateTag))
			{
				JMLog.error(out -> out.print(JMLog.STATE_DEFINITION_DUPLICATED, JMLog.getPackagelessName(machineTag), stateTag.getSimpleName()));
			}
			return new StateBuilderImpl(stateTag);
		}
		
		@Override
		public MachineBuilder onCreate(final Runnable work)
		{
			onCreate = work;
			return this;
		}
		
		@Override
		public MachineBuilder onPause(final Runnable work)
		{
			onPause = work;
			return this;
		}
		
		@Override
		public MachineBuilder onResume(final Runnable work)
		{
			onResume = work;
			return this;
		}
		
		@Override
		public MachineBuilder onStop(final Runnable work)
		{
			onStop = work;
			return this;
		}
		
		@Override
		public MachineBuilder onRestart(final Runnable work)
		{
			onRestart = work;
			return this;
		}
		
		@Override
		public MachineBuilder onTerminate(final Runnable work)
		{
			onTerminate = work;
			return this;
		}
		
		@Override
		public void build()
		{
			registrator.accept(JMMachine.Constructor.getNew(machineTag, startState, stateMap, onPause, onResume, onStop, onRestart, onTerminate));
			if (onCreate != null)
			{
				onCreate.run();
			}
		}
		
		@Override
		public void buildAndRun()
		{
			final JMMachine machine = JMMachine.Constructor.getNew(machineTag, startState, stateMap, onPause, onResume, onStop, onRestart, onTerminate);
			registrator.accept(machine);
			if (onCreate != null)
			{
				onCreate.run();
			}
			machine.run();
		}
		
		@Override
		public void buildAndPause()
		{
			final JMMachine machine = JMMachine.Constructor.getNew(machineTag, startState, stateMap, onPause, onResume, onStop, onRestart, onTerminate);
			registrator.accept(machine);
			if (onCreate != null)
			{
				onCreate.run();
			}
			machine.pause();
		}
		
		private class StateBuilderImpl implements StateBuilder
		{
			private final Class stateTag;
			private final JMState state;
			
			private StateBuilderImpl(final Class stateTag)
			{
				this.stateTag = stateTag;
				this.state = JMState.Constructor.getNew(machineTag, stateTag);
			}
			
			@Override
			public StateBuilder whenEnter(final Runnable defaultWork)
			{
				state.putEnterFunction(() ->
				{
					defaultWork.run();
					return null;
				});
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(final Supplier<Object> defaultWork)
			{
				state.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(final Runnable defaultWork)
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
			public WhenInputClasses whenInput(final Class... signals)
			{
				return new WhenInputImpl<>(signals);
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
				private final Enum[] signalsE;
				private final String[] signalsS;
				
				private WhenEnterImpl(final Class<S> signal)
				{
					this.signalC = signal;
					this.signalsE = null;
					this.signalsS = null;
				}
				
				private WhenEnterImpl(final Enum... signal)
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
				public StateBuilder doThis(final Runnable workOnEnter)
				{
					return doThis((S s) -> workOnEnter.run());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(final Consumer<S> workOnEnter)
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
						for (Enum signal : signalsE)
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
				public StateBuilder doThis(final Supplier<Object> workOnEnter)
				{
					return doThis((S s) -> workOnEnter.get());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(final Function<S, Object> workOnEnter)
				{
					if (signalC != null)
					{
						state.putEnterFunction(signalC, (Function<Object, Object>)workOnEnter);
					}
					else if (signalsE != null)
					{
						for (Enum signal : signalsE)
						{
							state.putEnterFunction(signal, (Function<Enum, Object>)workOnEnter);
						}
					}
					else if (signalsS != null)
					{
						for (String signal : signalsS)
						{
							state.putEnterFunction(signal, (Function<String, Object>)workOnEnter);
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
						for (Enum signal : signalsE)
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
				private WhenInputImpl(final Class... signal)
				{
					super(signal);
				}
				
				private WhenInputImpl(final Enum... signals)
				{
					super(signals);
				}
				
				private WhenInputImpl(final String... signals)
				{
					super(signals);
				}
				
				@Override
				public SwitchTo doThis(final Runnable workOnExit)
				{
					return doThis((S s) -> workOnExit.run());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchTo doThis(final Consumer<S> workOnExit)
				{
					if (signalsC != null)
					{
						for (Class signal : signalsC)
						{
							state.putExitFunction(signal, (Consumer<Object>)workOnExit);
						}
						return this;
					}
					else if (signalsE != null)
					{
						for (Enum signal : signalsE)
						{
							state.putExitFunction(signal, (Consumer<Enum>)workOnExit);
						}
						return this;
					}
					else
					{
						for (String signal : signalsS)
						{
							state.putExitFunction(signal, (Consumer<String>)workOnExit);
						}
						return this;
					}
				}
				
				@Override
				public SwitchTo doNothing()
				{
					if (signalsC != null)
					{
						for (Class signal : signalsC)
						{
							state.putExitFunction(signal, s ->
							{
								/* do nothing */
							});
						}
						return this;
					}
					else if (signalsE != null)
					{
						for (Enum signal : signalsE)
						{
							state.putExitFunction(signal, s ->
							{
								/* do nothing */
							});
						}
						return this;
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
						return this;
					}
				}
			}
			
			private class SwitchToImpl implements SwitchTo
			{
				final Class[] signalsC;
				final Enum[] signalsE;
				final String[] signalsS;
				
				SwitchToImpl(final Class... signals)
				{
					this.signalsC = signals;
					this.signalsE = null;
					this.signalsS = null;
				}
				
				SwitchToImpl(final Enum... signals)
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
				public StateBuilder switchTo(final Class stateTag)
				{
					if (signalsC != null)
					{
						for (Class signal : signalsC)
						{
							state.putSwitchRule(signal, stateTag);
						}
					}
					else if (signalsE != null)
					{
						for (Enum signal : signalsE)
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