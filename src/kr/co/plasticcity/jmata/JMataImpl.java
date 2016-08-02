package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;

import kr.co.plasticcity.jmata.function.*;

class JMataImpl
{
	/************************** ↓ Static Part **************************/
	
	private volatile static JMataImpl instance;
	
	static synchronized void initialize()
	{
		clearInstance();
	}
	
	static synchronized void release()
	{
		clearInstance();
	}
	
	private static synchronized void clearInstance()
	{
		if (instance != null)
		{
			for (JMMachine machine : instance.machineMap.values())
			{
				machine.terminateAll();
			}
			instance = null;
		}
	}
	
	static JMataImpl get()
	{
		if (instance == null)
		{
			synchronized (JMataImpl.class)
			{
				if (instance == null)
				{
					instance = new JMataImpl();
				}
			}
		}
		
		return instance;
	}
	
	/************************** ↑ Static Part **************************/
	
	private Map<Class<?>, JMMachine> machineMap;
	
	private Executor globalQue;
	
	private JMataImpl()
	{
		machineMap = new ConcurrentHashMap<>();
		globalQue = Executors.newSingleThreadExecutor();
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
						machineMap.get(machineTag).terminate(machineIdx);
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
						machineMap.get(machineTag).inputAll(signal);
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
						machineMap.get(machineTag).input(machineIdx, signal);
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