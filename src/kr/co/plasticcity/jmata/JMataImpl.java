package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.JMata.*;

class JMataImpl implements JMata
{
	/************************** ↓ Static Part **************************/
	
	private volatile static JMataImpl instance;
	
	static synchronized void initialize(int numThreads)
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
	
	private JMataImpl()
	{
		machineMap = new ConcurrentHashMap<Class<?>, JMMachine>();
	}
	
	public JMMachineBuilder buildMachine(Class<?> machineTag)
	{
		// TODO
		return null;
	}
	
	void runMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
	
	void stopMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
	
	void terminateMachine(Class<?> machineTag, int machineIdx)
	{
		// TODO
	}
}