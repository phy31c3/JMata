package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

class JMataImpl implements JMata
{
	/************************** ↓ Static Part **************************/
	
	private volatile static JMataImpl instance;
	
	static synchronized void initialize()
	{
		clearInstance();
	}
	
	static synchronized void exit()
	{
		clearInstance();
	}
	
	private static synchronized void clearInstance()
	{
		if (instance != null)
		{
			instance.machineMap.values().stream().forEach(m -> m.terminateAll());
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
	
	void buildMachine(Class<?> machineTag, Consumer<JMBuilder> builder)
	{
		globalQue.execute(() ->
		{
			builder.accept(JMBuilder.Constructor.getNew(machineTag, machineMap.containsKey(machineTag), machine ->
			{
				JMMachine oldMachine = machineMap.put(machineTag, machine);
				if (oldMachine != null)
				{
					oldMachine.terminateAll();
				}
			}));
		});
	}
	
	void runMachine(Class<?> machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).runAll();
			}
		});
	}
	
	void runMachine(Class<?> machineTag, int machineIdx)
	{
		globalQue.execute(() ->
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
		});
	}
	
	void stopMachine(Class<?> machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).stopAll();
			}
		});
	}
	
	void stopMachine(Class<?> machineTag, int machineIdx)
	{
		globalQue.execute(() ->
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
		});
	}
	
	void terminateMachine(Class<?> machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).terminateAll();
			}
		});
	}
	
	void terminateMachine(Class<?> machineTag, int machineIdx)
	{
		globalQue.execute(() ->
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
		});
	}
	
	<S> void inputTo(Class<?> machineTag, S signal)
	{
		globalQue.execute(() ->
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
		});
	}
	
	<S> void inputTo(Class<?> machineTag, int machineIdx, S signal)
	{
		globalQue.execute(() ->
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
		});
	}
}