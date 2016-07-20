package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.JMMachineBuilder.*;
import kr.co.plasticcity.jmata.JMata.*;
import kr.co.plasticcity.jmata.function.*;

class JMataImpl implements JMata
{
	/************************** ↓ Static Part **************************/
	
	private volatile static JMataImpl instance;
	
	static synchronized void initialize()
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
	
	private Map<Class<?>, JMMachine<?>> machineMap;
	
	private Executor executor;
	
	private JMataImpl()
	{
		machineMap = new ConcurrentHashMap<>();
		executor = Executors.newSingleThreadExecutor();
	}
	
	<M> void buildMachine(Class<M> machineTag, Consumer<JMMachineBuilder> builder)
	{
		executor.execute(() -> {
			builder.accept(new JMMachineBuilderImpl<M>(machineMap.containsKey(machineTag), machine -> {
				machineMap.put(machineTag, machine);
			}));
		});
	}
	
	<M> void runMachine(Class<M> machineTag)
	{
		// TODO
	}
	
	<M> void runMachine(Class<M> machineTag, int machineIdx)
	{
		// TODO
	}
	
	<M> void stopMachine(Class<M> machineTag)
	{
		// TODO
	}
	
	<M> void stopMachine(Class<M> machineTag, int machineIdx)
	{
		// TODO
	}
	
	<M> void terminateMachine(Class<M> machineTag)
	{
		// TODO
	}
	
	<M> void terminateMachine(Class<M> machineTag, int machineIdx)
	{
		// TODO
	}
}