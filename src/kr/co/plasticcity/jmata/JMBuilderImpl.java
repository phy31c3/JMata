package kr.co.plasticcity.jmata;

import java.util.*;

import kr.co.plasticcity.jmata.JMBuilder.*;
import kr.co.plasticcity.jmata.JMBuilder.StateBuilder.*;
import kr.co.plasticcity.jmata.function.*;

class JMBuilderImpl implements JMBuilder
{
	private Object machineTag;
	private boolean present;
	private JMConsumer<JMMachine> consumer;
	
	JMBuilderImpl(Object machineTag, boolean isPresent, JMConsumer<JMMachine> consumer)
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
				JMLog.error("State '%s' 중복 정의", stateTag.getSimpleName());
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
				public StateBuilder justSwitchToSelf()
				{
					return justSwitchTo(stateTag);
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
			
			private class WhenInputImpl<S> extends JustSwitchToImpl implements WhenInput<S>
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
				public SwitchTo<S> doThis(JMConsumer<S> workOnExit)
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, workOnExit);
						return new SwitchToImpl<S>(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putExitFunction(signal, (JMConsumer<Enum<?>>)workOnExit);
						}
						return new SwitchToImpl<S>(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putExitFunction(signal, (JMConsumer<String>)workOnExit);
						}
						return new SwitchToImpl<S>(signalsS);
					}
				}
				
				@Override
				@SuppressWarnings("unchecked")
				public SwitchTo<S> doThis(JMBiConsumer<S, Integer> workOnExit)
				{
					if (signalC != null)
					{
						stateCreater.putExitFunction(signalC, workOnExit);
						return new SwitchToImpl<S>(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putExitFunction(signal, (JMBiConsumer<Enum<?>, Integer>)workOnExit);
						}
						return new SwitchToImpl<S>(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putExitFunction(signal, (JMBiConsumer<String, Integer>)workOnExit);
						}
						return new SwitchToImpl<S>(signalsS);
					}
				}
				
				@Override
				public SwitchTo<S> doNothing()
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
						return new SwitchToImpl<S>(signalC);
					}
					else if (signalsE != null)
					{
						for (Enum<?> signal : signalsE)
						{
							stateCreater.putExitFunction(signal, new JMConsumer<Enum<?>>()
							{
								@Override
								public void accept(Enum<?> s)
								{
									/* do nothing */
								}
							});
						}
						return new SwitchToImpl<S>(signalsE);
					}
					else
					{
						for (String signal : signalsS)
						{
							stateCreater.putExitFunction(signal, new JMConsumer<String>()
							{
								@Override
								public void accept(String s)
								{
									/* do nothing */
								}
							});
						}
						return new SwitchToImpl<S>(signalsS);
					}
				}
			}
			
			private class SwitchToImpl<S> extends JustSwitchToImpl implements SwitchTo<S>
			{
				private SwitchToImpl(Class<S> signal)
				{
					super(signal);
				}
				
				private SwitchToImpl(Enum<?>... signals)
				{
					super(signals);
				}
				
				private SwitchToImpl(String... signals)
				{
					super(signals);
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
						stateCreater.putSwitchRule(signalsC[0], stateTag);
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
		}
	}
}