package kr.co.plasticcity.jmata;

import java.util.*;
import java.util.concurrent.*;

import kr.co.plasticcity.jmata.function.*;

class JMataImpl
{
	/* ================================== ↓ Static Part ================================== */
	
	private static final int NUM_PERMITS = Runtime.getRuntime().availableProcessors();
	private static final Semaphore permit = new Semaphore(NUM_PERMITS, true);
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
			permit.acquire(NUM_PERMITS);
			
			try
			{
				JMLog.setLogger(debugLogger, errorLogger);
				clearInstance();
				instance = new JMataImpl();
				state = STATE.RUNNING;
			}
			finally
			{
				permit.release(NUM_PERMITS);
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
			permit.acquire(NUM_PERMITS);
			
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
				permit.release(NUM_PERMITS);
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
						JMLog.error("JMata 초기화 오류 : 최초 JMata.initialize()를 호출해주세요.");
						break;
					case RUNNING:
						JMLog.error("알 수 없는 오류 발생 : JMata가 RUNNIG 상태이나 instance == null");
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
	
	/* ================================== ↑ Static Part ================================== */
	
	private final Map<Object, JMMachine> machineMap;
	private final ExecutorService globalQue;
	
	private JMataImpl()
	{
		this.machineMap = new ConcurrentHashMap<>();
		this.globalQue = Executors.newSingleThreadExecutor();
		this.globalQue.execute(() -> Thread.currentThread().setName("JMataGlobalThread"));
	}
	
	void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
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
	
	void runMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).runAll();
			}
		});
	}
	
	void runMachine(final Object machineTag, final int machineIdx)
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
	
	void stopMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).stopAll();
			}
		});
	}
	
	void stopMachine(final Object machineTag, final int machineIdx)
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
	
	void terminateMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).terminateAll();
				machineMap.remove(machineTag);
			}
		});
	}
	
	void terminateMachine(final Object machineTag, final int machineIdx)
	{
		globalQue.execute(() ->
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
		});
	}
	
	<S> void inputTo(final Object machineTag, final S signal)
	{
		globalQue.execute(() ->
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
		});
	}
	
	<S> void inputTo(final Object machineTag, final int machineIdx, final S signal)
	{
		globalQue.execute(() ->
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
		});
	}
}