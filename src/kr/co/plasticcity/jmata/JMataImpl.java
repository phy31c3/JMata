package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.JMBuilder.*;
import kr.co.plasticcity.jmata.JMata.*;
import kr.co.plasticcity.jmata.function.*;

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
			instance.machineMap.values().stream().forEach(m -> m.terminate());
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
	
	private Executor executor;
	
	private JMataImpl()
	{
		machineMap = new ConcurrentHashMap<>();
		executor = Executors.newSingleThreadExecutor();
	}
	
	void buildMachine(Class<?> machineTag, Consumer<JMBuilder> builder)
	{
		executor.execute(() -> {
			builder.accept(JMBuilder.Constructor.getNew(machineMap.containsKey(machineTag), machine -> {
				machineMap.put(machineTag, machine);
			}));
		});
	}
	
	void runMachine(Class<?> machineTag)
	{
		// TODO
	}
	
	void runMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
	
	void stopMachine(Class<?> machineTag)
	{
		// TODO
	}
	
	void stopMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
	
	void terminateMachine(Class<?> machineTag)
	{
		// TODO
	}
	
	void terminateMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
}