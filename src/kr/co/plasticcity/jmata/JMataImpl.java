package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;

import kr.co.plasticcity.jmata.function.*;

class JMataImpl
{
	/************************** ↓ Static Part **************************/
	
	private static final Semaphore permit = new Semaphore(1, true);
	private static volatile JMataImpl instance;
	private static volatile STATE state = STATE.NOT_INIT;
	
	private enum STATE
	{
		NOT_INIT, RUNNING, RELEASED;
	}
	
	static void initialize(JMConsumer<String> debugLogger, JMConsumer<String> errorLogger)
	{
		try
		{
			permit.acquire();
			
			try
			{
				JMLog.setLogger(debugLogger, errorLogger);
				clearInstance();
				instance = new JMataImpl();
				state = STATE.RUNNING;
			}
			finally
			{
				permit.release();
			}
		}
		catch (InterruptedException e)
		{
			initialize(debugLogger, errorLogger);
		}
	}
	
	static void release()
	{
		try
		{
			permit.acquire();
			
			try
			{
				if (state == STATE.RUNNING)
				{
					JMLog.setLogger(null, null);
					clearInstance();
					state = STATE.RELEASED;
				}
			}
			finally
			{
				permit.release();
			}
		}
		catch (InterruptedException e)
		{
			release();
		}
	}
	
	static void post(JMConsumer<JMataImpl> func)
	{
		try
		{
			permit.acquire();
			
			try
			{
				if (instance != null)
				{
					func.accept(instance);
				}
				else
				{
					switch (state)
					{
					case NOT_INIT:
						JMLog.debug("JMata 초기화 오류 : 최초 JMata.initialize()를 호출해주세요.");
						break;
					case RUNNING:
						JMLog.debug("알 수 없는 오류 발생 : JMata가 RUNNIG 상태이나 instance == null");
						break;
					case RELEASED:
						/* do nothing */
						break;
					}
				}
			}
			finally
			{
				permit.release();
			}
		}
		catch (InterruptedException e)
		{
			/* do nothing */
		}
	}
	
	private static void clearInstance()
	{
		if (instance != null)
		{
			instance.globalQue.shutdownNow();
			for (JMMachine machine : instance.machineMap.values())
			{
				machine.terminateAll();
			}
			instance = null;
		}
	}
	
	/************************** ↑ Static Part **************************/
	
	private final Map<Class<?>, JMMachine> machineMap;
	private final ExecutorService globalQue;
	
	private JMataImpl()
	{
		this.machineMap = new ConcurrentHashMap<>();
		this.globalQue = Executors.newSingleThreadExecutor();
	}
	
	void buildMachine(final Class<?> machineTag, final JMConsumer<JMBuilder> builder)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				builder.accept(JMBuilder.Constructor.getNew(machineTag, machineMap.containsKey(machineTag), new JMConsumer<JMMachine>()
				{
					@Override
					public void accept(JMMachine machine)
					{
						JMMachine oldMachine = machineMap.put(machineTag, machine);
						if (oldMachine != null)
						{
							oldMachine.terminateAll();
						}
					}
				}));
			}
		});
	}
	
	void runMachine(final Class<?> machineTag)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					machineMap.get(machineTag).runAll();
				}
			}
		});
	}
	
	void runMachine(final Class<?> machineTag, final int machineIdx)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					try
					{
						machineMap.get(machineTag).run(machineIdx);
					}
					catch (JMException e)
					{
						e.printJMLog();
					}
				}
			}
		});
	}
	
	void stopMachine(final Class<?> machineTag)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					machineMap.get(machineTag).stopAll();
				}
			}
		});
	}
	
	void stopMachine(final Class<?> machineTag, final int machineIdx)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					try
					{
						machineMap.get(machineTag).stop(machineIdx);
					}
					catch (JMException e)
					{
						e.printJMLog();
					}
				}
			}
		});
	}
	
	void terminateMachine(final Class<?> machineTag)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					machineMap.get(machineTag).terminateAll();
					machineMap.remove(machineTag);
				}
			}
		});
	}
	
	void terminateMachine(final Class<?> machineTag, final int machineIdx)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					try
					{
						if (machineMap.get(machineTag).terminate(machineIdx))
						{
							machineMap.remove(machineTag);
						}
					}
					catch (JMException e)
					{
						e.printJMLog();
					}
				}
			}
		});
	}
	
	<S> void inputTo(final Class<?> machineTag, final S signal)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					try
					{
						if (signal instanceof String)
						{
							machineMap.get(machineTag).inputToAll((String)signal);
						}
						else if (signal instanceof Enum)
						{
							machineMap.get(machineTag).inputToAll((Enum<?>)signal);
						}
						else
						{
							machineMap.get(machineTag).inputToAll(signal);
						}
					}
					catch (JMException e)
					{
						e.printJMLog();
					}
				}
			}
		});
	}
	
	<S> void inputTo(final Class<?> machineTag, final int machineIdx, final S signal)
	{
		globalQue.execute(new Runnable()
		{
			@Override
			public void run()
			{
				if (machineMap.containsKey(machineTag))
				{
					try
					{
						if (signal instanceof String)
						{
							machineMap.get(machineTag).input(machineIdx, (String)signal);
						}
						else if (signal instanceof Enum)
						{
							machineMap.get(machineTag).input(machineIdx, (Enum<?>)signal);
						}
						else
						{
							machineMap.get(machineTag).input(machineIdx, signal);
						}
					}
					catch (JMException e)
					{
						e.printJMLog();
					}
				}
			}
		});
	}
}