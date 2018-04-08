package kr.co.plasticcity.jmata;

import java.util.HashMap;
import java.util.Map;

import kr.co.plasticcity.jmata.function.Consumer;
import kr.co.plasticcity.jmata.function.Function;
import kr.co.plasticcity.jmata.function.Supplier;

class JMBuilderImpl implements JMBuilder.Builder
{
	private final String machineName;
	private final boolean present;
	private final Consumer<JMMachine> registrator;
	
	JMBuilderImpl(final String machineName, final boolean isPresent, final Consumer<JMMachine> registrator)
	{
		this.machineName = machineName;
		this.present = isPresent;
		this.registrator = registrator;
	}
	
	@Override
	public AndDo ifPresentThenIgnoreThis(final Consumer<BaseDefiner> definer)
	{
		if (present)
		{
			JMLog.debug(out -> out.print(JMLog.IGNORE_MACHINE_BUILD, machineName));
		}
		else
		{
			definer.accept(new MachineBuilderImpl(JMMachineImpl::new));
		}
		return new AndDoImpl();
	}
	
	@Override
	public AndDo ifPresentThenReplaceWithThis(final Consumer<BaseDefiner> definer)
	{
		if (present)
		{
			JMLog.debug(out -> out.print(JMLog.REPLACE_MACHINE, machineName));
		}
		definer.accept(new MachineBuilderImpl(JMMachineImpl::new));
		return new AndDoImpl();
	}
	
	private class AndDoImpl implements AndDo
	{
		@Override
		public void andDo(final Runnable work)
		{
			if (present)
			{
				work.run();
			}
		}
	}
	
	private class MachineBuilderImpl implements BaseDefiner, MachineBuilder
	{
		private final MachineSupplier machineSupplier;
		private final Map<Class, JMState> stateMap;
		private Class startState;
		private Runnable onCreate;
		private Runnable onPause;
		private Runnable onResume;
		private Runnable onStop;
		private Runnable onRestart;
		private Runnable onTerminate;
		private boolean isLogEnabled;
		
		private MachineBuilderImpl(final MachineSupplier machineSupplier)
		{
			JMLog.debug(out -> out.print(JMLog.MACHINE_BUILD_STARTED, machineName));
			this.machineSupplier = machineSupplier;
			this.stateMap = new HashMap<>();
			this.isLogEnabled = true;
		}
		
		@Override
		public StateBuilder<StartDefiner> defineBaseRule()
		{
			return new StateBuilderImpl<>(this, BaseDefiner.class);
		}
		
		@Override
		public StateBuilder<MachineBuilder> defineStartState(final Class stateTag)
		{
			startState = stateTag;
			return defineState(stateTag);
		}
		
		@Override
		public StateBuilder<MachineBuilder> defineState(final Class stateTag)
		{
			if (stateMap.containsKey(stateTag))
			{
				JMLog.error(out -> out.print(JMLog.STATE_DEFINITION_DUPLICATED, machineName, stateTag.getSimpleName()));
			}
			return new StateBuilderImpl<>(this, stateTag);
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
		public MachineBuilder setLogEnabled(final boolean enabled)
		{
			isLogEnabled = enabled;
			return this;
		}
		
		@Override
		public void build()
		{
			buildMachine();
		}
		
		@Override
		public void buildAndRun()
		{
			buildMachine().run();
		}
		
		@Override
		public void buildAndPause()
		{
			buildMachine().pause();
		}
		
		@Override
		public void buildAndStop()
		{
			buildMachine().stop();
		}
		
		private JMMachine buildMachine()
		{
			applyBaseRule();
			final JMMachine machine = machineSupplier.createMachine(machineName, startState, stateMap, onPause, onResume, onStop, onRestart, onTerminate);
			machine.setLogEnabled(isLogEnabled);
			JMLog.debug(out -> out.print(JMLog.MACHINE_BUILT, machineName));
			registrator.accept(machine);
			if (onCreate != null)
			{
				onCreate.run();
			}
			return machine;
		}
		
		private void applyBaseRule()
		{
			if (stateMap.containsKey(BaseDefiner.class))
			{
				final JMState base = stateMap.get(BaseDefiner.class);
				stateMap.remove(BaseDefiner.class);
				for (final JMState state : stateMap.values())
				{
					state.copyFrom(base);
				}
			}
			stateMap.put(null, null); // for dontSwitch()
		}
		
		private class StateBuilderImpl<R> implements StateBuilder<R>
		{
			private final R r;
			private final Class stateTag;
			private final JMState state;
			
			private StateBuilderImpl(final R r, final Class stateTag)
			{
				this.r = r;
				this.stateTag = stateTag;
				this.state = new JMStateImpl(machineName, stateTag);
			}
			
			@Override
			public StateBuilder<R> whenEnter(final Runnable defaultWork)
			{
				state.putEnterFunction(() ->
				{
					defaultWork.run();
					return null;
				});
				return this;
			}
			
			@Override
			public StateBuilder<R> whenEnter(final Supplier<Object> defaultWork)
			{
				state.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder<R> whenExit(final Runnable defaultWork)
			{
				state.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public <S> WhenEnter<S, R> whenEnterBy(final Class<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnterPrimitive<S, R> whenEnterBy(final Enum<S> signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenEnterPrimitive<S, R> whenEnterBy(final S[] signals)
			{
				return new WhenEnterImpl<>(signals);
			}
			
			@Override
			public WhenEnterPrimitive<String, R> whenEnterBy(final String signal)
			{
				return new WhenEnterImpl<>(signal);
			}
			
			@Override
			public WhenEnterPrimitive<String, R> whenEnterBy(final String... signals)
			{
				return new WhenEnterImpl<>(signals);
			}
			
			@Override
			public <S> WhenInput<S, R> whenInput(final Class<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public WhenInputClasses<R> whenInput(final Class... signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public <S extends Enum<S>> WhenInputPrimitive<S, R> whenInput(final Enum<S> signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public <S extends Enum<S>> WhenInputPrimitive<S, R> whenInput(final S[] signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public WhenInputPrimitive<String, R> whenInput(final String signal)
			{
				return new WhenInputImpl<>(signal);
			}
			
			@Override
			public WhenInputPrimitive<String, R> whenInput(final String... signals)
			{
				return new WhenInputImpl<>(signals);
			}
			
			@Override
			public R apply()
			{
				stateMap.put(stateTag, state);
				return r;
			}
			
			private class WhenEnterImpl<S> implements WhenEnterPrimitive<S, R>
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
				@SuppressWarnings("unchecked")
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
				@SuppressWarnings("unchecked")
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
				@SuppressWarnings("unchecked")
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
			
			private class WhenInputImpl<S> extends SwitchToImpl implements WhenInputPrimitive<S, R>
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
				@SuppressWarnings("unchecked")
				public SwitchOrNot doThis(final Runnable workOnExit)
				{
					return doThis((S s) -> workOnExit.run());
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchOrNot doThis(final Consumer<S> workOnExit)
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
					else if (signalsS != null)
					{
						for (String signal : signalsS)
						{
							state.putExitFunction(signal, (Consumer<String>)workOnExit);
						}
						return this;
					}
					else
					{
						/* Inaccessible block */
						return this;
					}
				}
				
				@Override
				@SuppressWarnings("unchecked")
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
					else if (signalsS != null)
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
					else
					{
						/* Inaccessible block */
						return this;
					}
				}
			}
			
			private class SwitchToImpl implements SwitchOrNot<R>
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
				@SuppressWarnings("unchecked")
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
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder switchToSelf()
				{
					return switchTo(stateTag);
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder dontSwitch()
				{
					return switchTo(null);
				}
			}
		}
	}
	
	@FunctionalInterface
	private interface MachineSupplier
	{
		JMMachine createMachine(final String name, final Class startState, final Map<Class, ? extends JMState> stateMap,
		                        final Runnable onPause, final Runnable onResume, final Runnable onStop, final Runnable onRestart, final Runnable onTerminate);
	}
	
	/* ================================== Instant Builder ================================== */
	
	static class InstantBuilderImpl extends JMBuilderImpl
	{
		InstantBuilderImpl(final String machineName, final Consumer<JMMachine> registrator)
		{
			super(machineName, false, registrator);
		}
		
		@Override
		public AndDo ifPresentThenIgnoreThis(final Consumer<BaseDefiner> definer)
		{
			definer.accept(new MachineBuilderImpl(JMMachineInstantImpl::new));
			return null;
		}
	}
}