package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.function.*;

class JMBuilderImpl implements JMBuilder
{
	private Class<?> machineTag;
	private boolean present;
	private JMConsumer<JMMachine> consumer;
	
	JMBuilderImpl(Class<?> machineTag, boolean isPresent, JMConsumer<JMMachine> consumer)
	{
		this.machineTag = machineTag;
		this.present = isPresent;
		this.consumer = consumer;
	}
	
	@Override
	public void ifPresentThenIgnoreThis(JMConsumer<StartStateDefiner> definer)
	{
		if (present)
		{
			return;
		}
		else
		{
			definer.accept(new MachineBuilderImpl());
		}
	}
	
	@Override
	public void ifPresentThenReplaceToThis(JMConsumer<StartStateDefiner> definer)
	{
		definer.accept(new MachineBuilderImpl());
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
			consumer.accept(JMMachine.Constructor.getNew(machineTag, 1, startState, stateMap));
		}
		
		@Override
		public void build(int numMachines)
		{
			consumer.accept(JMMachine.Constructor.getNew(machineTag, numMachines, startState, stateMap));
		}
		
		@Override
		public void buildAndRun()
		{
			JMMachine machine = JMMachine.Constructor.getNew(machineTag, 1, startState, stateMap);
			consumer.accept(machine);
			machine.runAll();
		}
		
		@Override
		public void buildAndRun(int numMachines)
		{
			JMMachine machine = JMMachine.Constructor.getNew(machineTag, numMachines, startState, stateMap);
			consumer.accept(machine);
			machine.runAll();
		}
		
		private class StateBuilderImpl implements StateBuilder
		{
			private Class<?> stateTag;
			private JMStateCreater stateCreater;
			
			private StateBuilderImpl(Class<?> tag)
			{
				this.stateTag = tag;
				this.stateCreater = JMStateCreater.Constructor.getNew(tag);
			}
			
			@Override
			public StateBuilder whenEnter(JMVoidConsumer defaultWork)
			{
				stateCreater.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenEnter(JMConsumer<Integer> defaultWork)
			{
				stateCreater.putEnterFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(JMVoidConsumer defaultWork)
			{
				stateCreater.putExitFunction(defaultWork);
				return this;
			}
			
			@Override
			public StateBuilder whenExit(JMConsumer<Integer> defaultWork)
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
			public JustSwitchTo whenInput(Class<?>... signals)
			{
				return new JustSwitchToImpl(signals);
			}
			
			@Override
			public JustSwitchTo whenInput(Enum<?>... signals)
			{
				return new JustSwitchToImpl(signals);
			}
			
			@Override
			public JustSwitchTo whenInput(String... signals)
			{
				return new JustSwitchToImpl(signals);
			}
			
			@Override
			public <S> SwitchTo<S> whenInput(Class<S> signal)
			{
				return new SwitchToImpl<S>(signal);
			}
			
			@Override
			public <S extends Enum<S>> SwitchTo<S> whenInput(Enum<S> signal)
			{
				return new SwitchToImpl<S>(signal);
			}
			
			@Override
			public SwitchTo<String> whenInput(String signal)
			{
				return new SwitchToImpl<String>(signal);
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
						stateCreater.putEnterFunction(signalC, workOnEnter);
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, (JMConsumer<Enum<?>>)workOnEnter);
					}
					else
					{
						stateCreater.putEnterFunction(signalS, (JMConsumer<String>)workOnEnter);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder doThis(JMBiConsumer<S, Integer> workOnEnter)
				{
					if (signalC != null)
					{
						stateCreater.putEnterFunction(signalC, workOnEnter);
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, (JMBiConsumer<Enum<?>, Integer>)workOnEnter);
					}
					else
					{
						stateCreater.putEnterFunction(signalS, (JMBiConsumer<String, Integer>)workOnEnter);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder doNothing()
				{
					if (signalC != null)
					{
						stateCreater.putEnterFunction(signalC, new JMConsumer<S>()
						{
							@Override
							public void accept(S s)
							{
								/* do nothing */
							}
						});
					}
					else if (signalE != null)
					{
						stateCreater.putEnterFunction(signalE, new JMConsumer<Enum<?>>()
						{
							@Override
							public void accept(Enum<?> s)
							{
								/* do nothing */
							}
						});
					}
					else
					{
						stateCreater.putEnterFunction(signalS, new JMConsumer<String>()
						{
							@Override
							public void accept(String s)
							{
								/* do nothing */
							}
						});
					}
					return StateBuilderImpl.this;
				}
			}
			
			private class JustSwitchToImpl implements JustSwitchTo
			{
				protected Class<?>[] signalsC;
				protected Enum<?>[] signalsE;
				protected String[] signalsS;
				
				protected JustSwitchToImpl(Class<?>... signals)
				{
					this.signalsC = signals;
				}
				
				protected JustSwitchToImpl(Enum<?>... signals)
				{
					this.signalsE = signals;
				}
				
				protected JustSwitchToImpl(String... signals)
				{
					this.signalsS = signals;
				}
				
				@Override
				public StateBuilder justSwitchTo(Class<?> stateTag)
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
			
			private class SwitchToImpl<S> extends JustSwitchToImpl implements SwitchTo<S>
			{
				private SwitchToImpl(Class<S> signal)
				{
					super(signal);
				}
				
				private SwitchToImpl(Enum<?> signal)
				{
					super(signal);
				}
				
				private SwitchToImpl(String signal)
				{
					super(signal);
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public WhenExit<S> switchTo(Class<?> stateTag)
				{
					if (signalsC != null)
					{
						stateCreater.putSwitchRule(signalsC[0], stateTag);
						return new WhenExitImpl<S>((Class<S>)signalsC[0]);
					}
					else if (signalsE != null)
					{
						stateCreater.putSwitchRule(signalsE[0], stateTag);
						return new WhenExitImpl<S>(signalsE[0]);
					}
					else
					{
						stateCreater.putSwitchRule(signalsS[0], stateTag);
						return new WhenExitImpl<S>(signalsS[0]);
					}
				}
			}
			
			private class WhenExitImpl<S> implements WhenExit<S>
			{
				private Class<S> signalC;
				private Enum<?> signalE;
				private String signalS;
				
				private WhenExitImpl(Class<S> signal)
				{
					this.signalC = signal;
				}
				
				private WhenExitImpl(Enum<?> signal)
				{
					this.signalE = signal;
				}
				
				private WhenExitImpl(String signal)
				{
					this.signalS = signal;
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder AndDo(JMConsumer<S> workOnExit)
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, workOnExit);
					}
					else if (signalE != null)
					{
						stateCreater.putExitFunction(signalE, (JMConsumer<Enum<?>>)workOnExit);
					}
					else
					{
						stateCreater.putExitFunction(signalS, (JMConsumer<String>)workOnExit);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public StateBuilder AndDo(JMBiConsumer<S, Integer> workOnExit)
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, workOnExit);
					}
					else if (signalE != null)
					{
						stateCreater.putExitFunction(signalE, (JMBiConsumer<Enum<?>, Integer>)workOnExit);
					}
					else
					{
						stateCreater.putExitFunction(signalS, (JMBiConsumer<String, Integer>)workOnExit);
					}
					return StateBuilderImpl.this;
				}
				
				@Override
				public StateBuilder AndDoNothing()
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, new JMConsumer<S>()
						{
							@Override
							public void accept(S s)
							{
								/* do nothing */
							}
						});
					}
					else if (signalE != null)
					{
						stateCreater.putExitFunction(signalE, new JMConsumer<Enum<?>>()
						{
							@Override
							public void accept(Enum<?> s)
							{
								/* do nothing */
							}
						});
					}
					else
					{
						stateCreater.putExitFunction(signalS, new JMConsumer<String>()
						{
							@Override
							public void accept(String s)
							{
								/* do nothing */
							}
						});
					}
					return StateBuilderImpl.this;
				}
			}
		}
	}
}