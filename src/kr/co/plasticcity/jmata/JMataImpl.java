package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import kr.co.plasticcity.jmata.JMata.*;

class JMataImpl implements JMata
{
	/************************** ↓ Static Part **************************/
	
	private volatile static JMataImpl instance;
	
	static synchronized void init()
	{
		if (instance != null)
		{
			instance.machineMap.values().stream().forEach(m -> m.terminate());
			instance = null;
		}
	}
	
	static JMata get()
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
	
	public void initialize()
	{
		synchronized (JMataImpl.class)
		{
			instance = null;
			get();
		}
	}
	
	public JMMachine createMachine(Class<?> tag)
	{
		// TODO
		return null;
	}
	
	public MachineBuilderOptional buildMachine(Class<?> tag)
	{
		// TODO
		return null;
	}
	
	private class MachineOptionalImpl implements MachineBuilderOptional
	{
		@Override
		public JMMachine takeAnyway()
		{
			// TODO
			return null;
		}
		
		@Override
		public void ifPresent(Consumer<JMMachine> func)
		{
			// TODO
		}
	}
}