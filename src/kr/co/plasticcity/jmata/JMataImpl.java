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
				JMLog.debug("** JMata has been initialized");
				permit.release(NUM_PERMITS);
			}
		}
		catch (InterruptedException e)
		{
			initialize(debugLogger, errorLogger);
			Thread.currentThread().interrupt();
		}
	}
	
	static void release(final JMVoidConsumer releaseWork)
	{
		try
		{
			permit.acquire(NUM_PERMITS);
			
			try
			{
				if (state == STATE.RUNNING)
				{
					clearInstance();
					state = STATE.RELEASED;
					JMLog.debug("** JMata has been released");
					JMLog.setLogger(null, null);
					
					if (releaseWork != null)
					{
						releaseWork.accept();
					}
				}
			}
			finally
			{
				permit.release(NUM_PERMITS);
			}
		}
		catch (InterruptedException e)
		{
			release(releaseWork);
			Thread.currentThread().interrupt();
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
						JMLog.error("** JMata initialization error : Call JMata.initialize() first");
						break;
					case RUNNING:
						JMLog.error("** JMata unknown error : JMata is in RUNNIG state, but instance == null");
						break;
					case RELEASED:
						JMLog.debug("** JMata already released : JMata is released, but JMata command is called");
						break;
					default:
						JMLog.error("** JMata undefined state : %s", state.name());
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
			Thread.currentThread().interrupt();
		}
		catch (RejectedExecutionException e)
		{
			JMLog.error("** JMata RejectedExecutionException occurred");
		}
	}
	
	private static void clearInstance()
	{
		if (instance != null)
		{
			for (JMMachine machine : instance.machineMap.values())
			{
				machine.terminate();
			}
			instance.globalQue.shutdownNow();
			instance = null;
		}
	}
	
	/* ================================== ↑ Static Part ================================== */
	
	private final Map<Object, JMMachine> machineMap;
	private final ExecutorService globalQue;
	
	private JMataImpl()
	{
		this.machineMap = new ConcurrentHashMap<>();
		this.globalQue = Executors.newSingleThreadExecutor(r ->
		{
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName("JMataGlobalThread");
			return t;
		});
	}
	
	void buildMachine(final Object machineTag, final JMConsumer<JMBuilder> builder)
	{
		globalQue.execute(() ->
		{
			JMLog.debug("[%s] machine build started", machineTag);
			builder.accept(JMBuilder.Constructor.getNew(machineTag, machineMap.containsKey(machineTag), machine ->
			{
				JMMachine oldMachine = machineMap.put(machineTag, machine);
				if (oldMachine != null)
				{
					oldMachine.terminate();
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
				machineMap.get(machineTag).run();
			}
		});
	}
	
	void stopMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).stop();
			}
		});
	}
	
	void terminateMachine(final Object machineTag)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).terminate();
				machineMap.remove(machineTag);
			}
		});
	}
	
	<S> void inputTo(final Object machineTag, final S signal)
	{
		globalQue.execute(() ->
		{
			if (machineMap.containsKey(machineTag))
			{
				machineMap.get(machineTag).input(signal);
			}
		});
	}
}